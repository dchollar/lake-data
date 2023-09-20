package com.lake

import com.lake.service.ReporterService
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

@CompileStatic
@Configuration
class WebSecurityConfig {

    private static final String SESSION_COOKIE_NAME = 'JSESSIONID'

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    AuthenticationProvider authenticationProvider(ReporterService reporterService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider()
        provider.setUserDetailsService(reporterService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) {
        http.csrf(csrf -> csrf.disable()).headers {
            it.frameOptions { it.disable() }
        }.authorizeHttpRequests {
            //it.requestMatchers('/', '/index', '/home', '/public/**', '/favicon.ico').permitAll().anyRequest().authenticated()
            it.requestMatchers(mvc.pattern(HttpMethod.GET, '/')).permitAll()
            it.requestMatchers(mvc.pattern(HttpMethod.GET, '/home')).permitAll()
            it.requestMatchers(mvc.pattern(HttpMethod.GET, '/favicon.ico')).permitAll()
            it.requestMatchers(mvc.pattern( '/public/**')).permitAll()
            it.anyRequest().authenticated()
        }.formLogin {
            it.permitAll()
        }.logout {
            it.permitAll().deleteCookies(SESSION_COOKIE_NAME).invalidateHttpSession(true).logoutSuccessUrl('/')
        }.rememberMe(Customizer.withDefaults())
        return http.build()
    }

}
