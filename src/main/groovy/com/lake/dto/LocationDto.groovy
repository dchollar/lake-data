package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['description'])
class LocationDto {
    Integer id
    String description
    SiteDto site
    Set<UnitDto> units = new TreeSet<>()
}
