package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['description', 'id'])
class LocationDto {
    Integer id
    String description
    String comment
    Integer siteId
    String siteDescription
    Set<UnitDto> units = new TreeSet<>()
}
