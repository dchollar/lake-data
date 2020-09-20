package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode
@Sortable(includes = ['description'])
class LocationDto {
    Integer id
    String description
}
