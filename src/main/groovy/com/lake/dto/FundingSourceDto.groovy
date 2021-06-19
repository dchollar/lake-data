package com.lake.dto

import com.lake.entity.FundingSourceType
import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

import java.time.LocalDate

@CompileStatic
@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['name', 'id'])
class FundingSourceDto {
    Integer id
    String name
    String description
    LocalDate startDate
    LocalDate endDate
    FundingSourceType type
}
