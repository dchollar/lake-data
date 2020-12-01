package com.lake.dto

import com.lake.entity.UnitType
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import groovy.transform.ToString

import java.time.LocalDate

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteId', 'unitId', 'locationId', 'collectionDate', 'depth', 'id'])
@ToString
class SavedMeasurementDto {
    Integer id
    LocalDate collectionDate
    BigDecimal value
    BigDecimal depth
    String comment
    Integer locationId
    Integer unitId
    Integer siteId
    UnitType unitType
}
