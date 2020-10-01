package com.lake.dto

import com.lake.entity.RoleType
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['firstName', 'lastName', 'id'])
class ReporterDto {
    Integer id
    String firstName
    String lastName
    String emailAddress
    String phoneNumber
    String username
    String password
    Boolean enabled
    Set<RoleType> roles
}
