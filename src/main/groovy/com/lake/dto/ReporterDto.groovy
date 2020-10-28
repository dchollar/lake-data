package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['firstName', 'lastName', 'id'])
class ReporterDto {
    Integer id
    String firstName
    String lastName
    String emailAddress
    String username
    String password
    Boolean enabled
    Boolean roleReporter
    Boolean rolePowerUser
    Boolean roleAdmin
}
