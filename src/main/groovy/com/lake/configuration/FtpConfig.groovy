package com.lake.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "ftp")
class FtpConfig {
    File uploadLocation
    File processLocation
    File archiveLocation
}
