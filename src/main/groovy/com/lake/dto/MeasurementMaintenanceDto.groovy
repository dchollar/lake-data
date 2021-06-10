package com.lake.dto

import com.lake.entity.CharacteristicType
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import groovy.transform.ToString

import java.time.LocalDate

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteId', 'characteristicId', 'locationId', 'collectionDate', 'depth', 'id'])
@ToString
class MeasurementMaintenanceDto {
    Integer id
    LocalDate collectionDate
    BigDecimal value
    BigDecimal depth
    String comment
    Integer locationId
    Integer characteristicId
    Integer siteId
    Integer fundingSourceId
    CharacteristicType characteristicType
    String reporterName
}
