package com.lake.configuration

import groovy.transform.CompileStatic
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@CompileStatic
@Configuration
@ConfigurationProperties(prefix = "ftp")
class FtpConfig {
    File uploadLocation
    File processLocation
    File archiveLocation
    File errorLocation
}
