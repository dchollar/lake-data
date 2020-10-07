package com.lake.dto


import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id', 'description'])
@Sortable(includes = ['description', 'id'])
class SiteDto {
    Integer id
    String description
    String waterBodyNumber
    String dnrRegion
    String geoRegion
    Collection<LocationDto> locations = new TreeSet<>()
}
