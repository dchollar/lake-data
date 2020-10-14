package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

import java.time.LocalDate

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['collectionDate', 'depth', 'id'])
class MeasurementDto {

    Integer id
    LocalDate collectionDate
    Integer dayOfYear
    BigDecimal value
    BigDecimal depth
    String comment
    UnitDto unit
    LocationDto location
    ReporterDto reporter

}
