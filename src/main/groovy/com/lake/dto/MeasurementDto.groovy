package com.lake.dto

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

import java.time.LocalDate

@CompileStatic
@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['collectionDate', 'depth', 'id'])
class MeasurementDto {

    Integer id
    LocalDate collectionDate
    Integer dayOfYear
    BigDecimal value
    BigDecimal depth
    String comment
    CharacteristicDto characteristic
    LocationDto location
    ReporterDto reporter
    FundingSourceDto fundingSource
}
