package com.lake

import groovy.transform.CompileStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@CompileStatic
@SpringBootApplication
@EnableJpaRepositories
@EnableCaching
@EnableScheduling
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableJpaAuditing
class LakeDataApplication {

    static void main(String[] args) {
        SpringApplication.run(LakeDataApplication.class, args)
    }

    @Bean
    Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage('com.lake'))
                .paths(PathSelectors.any())
                .build()
    }

}