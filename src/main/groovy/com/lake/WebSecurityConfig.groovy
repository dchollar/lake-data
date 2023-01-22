package com.lake

import com.lake.service.ReporterService
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@CompileStatic
@Configuration
class WebSecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    AuthenticationProvider authenticationProvider(ReporterService reporterService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService(reporterService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf().disable().authorizeHttpRequests {
            it.requestMatchers('/', '/index', '/home', '/public/**', '/js/**', '/ico/**', '/favicon.ico').permitAll().anyRequest().authenticated()
        }.formLogin {
            it.permitAll()
        }.logout {
            it.permitAll().deleteCookies('JSESSIONID').invalidateHttpSession(true).logoutSuccessUrl('/')
        }
                .rememberMe()
        return http.build()
    }

}
