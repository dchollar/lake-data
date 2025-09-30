package com.lake

import com.lake.service.ReporterService
import groovy.transform.CompileStatic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor
import org.springframework.security.web.SecurityFilterChain

@CompileStatic
@Configuration
class WebSecurityConfig {

    private static final String SESSION_COOKIE_NAME = 'JSESSIONID'

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    @Bean
    AuthenticationProvider authenticationProvider(ReporterService reporterService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(reporterService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http.csrf {
            it.disable()
        }.headers {
            it.frameOptions { it.disable() }
        }.authorizeHttpRequests {
            it.requestMatchers('/').permitAll()
            it.requestMatchers('/index').permitAll()
            it.requestMatchers('/home').permitAll()
            it.requestMatchers('/favicon.ico').permitAll()
            it.requestMatchers('/public/**').permitAll()
            it.requestMatchers('/actuator/health').permitAll()
            it.anyRequest().authenticated()
        }.formLogin {
            it.permitAll()
        }.logout {
            it.permitAll()
                    .deleteCookies(SESSION_COOKIE_NAME)
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl('/index')
        }
        return http.build()
    }

    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor()
        executor.setCorePoolSize(5)
        executor.setMaxPoolSize(10)
        executor.setQueueCapacity(25)
        executor.initialize()
        new DelegatingSecurityContextAsyncTaskExecutor(executor)
    }

}
