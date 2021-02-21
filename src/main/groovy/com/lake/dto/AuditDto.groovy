package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['created', 'id'], reversed = true)
class AuditDto {
    Integer id
    String created
    String httpMethod
    String endpoint
    String controller
    String reporterName
}
