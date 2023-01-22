package com.lake

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@CompileStatic
@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
@EnableScheduling
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@EnableJpaAuditing
@EnableConfigurationProperties
class LakeDataApplication {

    static void main(String[] args) {
        SpringApplication.run(LakeDataApplication.class, args)
    }

}