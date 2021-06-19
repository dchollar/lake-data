package com.lake.dto


import com.lake.entity.CharacteristicType
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

@CompileStatic
@EqualsAndHashCode(includes = ['id', 'description'])
@Sortable(includes = ['type', 'description', 'id'])
class CharacteristicDto {
    Integer id
    String unitDescription
    String description
    String shortDescription
    Boolean enableDepth
    CharacteristicType type
}
