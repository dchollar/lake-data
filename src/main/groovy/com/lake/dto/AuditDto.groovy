package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

import java.time.ZonedDateTime

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['created', 'id'], reversed = true)
class AuditDto {
    Integer id
    ZonedDateTime created
    String httpMethod
    String endpoint
    String controller
    String reporterName
}
