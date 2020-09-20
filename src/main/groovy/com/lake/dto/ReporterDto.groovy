package com.lake.dto

import com.lake.entity.RoleType

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
