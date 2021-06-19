package com.lake.dto

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@CompileStatic
@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteId', 'locationId', 'characteristicId', 'id'])
class CharacteristicLocationDto {
    Integer id
    Integer siteId
    Integer locationId
    Integer characteristicId
}
