package com.lake.dto

import java.time.LocalDate

class SavedMeasurementDto {
    LocalDate collectionDate
    BigDecimal value
    BigDecimal depth
    String comment
    Integer locationId
    Integer unitId
    Integer siteId
}
