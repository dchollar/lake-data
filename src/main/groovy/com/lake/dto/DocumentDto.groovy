package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable

import java.time.ZonedDateTime

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['path','title', 'id'])
class DocumentDto {
    Integer id
    String path
    String title
    Integer siteId
    ZonedDateTime created
    ZonedDateTime lastUpdated
    byte[] document
}
