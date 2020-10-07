package com.lake.dto


import com.lake.entity.UnitType
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@EqualsAndHashCode(includes = ['id', 'longDescription'])
@Sortable(includes = ['type', 'longDescription', 'id'])
class UnitDto {
    Integer id
    String unitDescription
    String longDescription
    String shortDescription
    Boolean enableDepth
    UnitType type
}
