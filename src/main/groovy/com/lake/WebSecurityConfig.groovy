package com.lake

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

import javax.sql.DataSource

@CompileStatic
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    void initialize(AuthenticationManagerBuilder builder, DataSource dataSource) {
        builder.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username,password,enabled from reporter where username = ?")
                .authoritiesByUsernameQuery("select mr.username,rr.role from reporter_role rr, reporter mr where mr.id = rr.reporter_id and mr.username = ?")
    }

    @Override
    protected void configure(HttpSecurity http) {
        http
                .csrf().disable()
                .rememberMe()
                .and().formLogin()
                .and().logout().logoutSuccessUrl('/')
                .and().httpBasic()
                .and().authorizeRequests()
                .antMatchers('/', '/index', '/home', '/public/api/**', '/js/**', '/ico/**', '/documents', '/privacy', '/favicon.ico').permitAll()
                .anyRequest().authenticated()
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
