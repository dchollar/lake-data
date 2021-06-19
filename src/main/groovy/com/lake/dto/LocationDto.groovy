package com.lake.dto

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@CompileStatic
@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteDescription', 'description', 'id'])
class LocationDto {
    Integer id
    String description
    String comment
    Integer siteId
    String siteDescription
    String latitude
    String longitude
}