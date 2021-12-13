package com.lake.entity

import groovy.transform.CompileStatic

@CompileStatic
enum RoleType {
    ROLE_REPORTER, // can only enter scouting data
    ROLE_POWER_USER, // can see results page, cannot do maintenance
    ROLE_DOCUMENT_ADMIN, // can fully administer documents only
    ROLE_ADMIN // can do and see everything
}