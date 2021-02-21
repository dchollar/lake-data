package com.lake

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

import javax.sql.DataSource

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    void initialize(AuthenticationManagerBuilder builder, DataSource dataSource) {
        builder.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username,password,enabled from reporter where username = ?")
                .authoritiesByUsernameQuery("select mr.username,rr.role from reporter_role rr, reporter mr where mr.id = rr.reporter_id and mr.username = ?")
    }

    @Override
    protected void configure(HttpSecurity http) {
        http.authorizeRequests()
                .antMatchers('/', '/index', '/public/api/**', '/js/**', '/documents').permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .and().httpBasic()
                .and().csrf().disable()
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }
}
