package com.lake.dto

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import org.springframework.web.multipart.MultipartFile

@CompileStatic
@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteId', 'path', 'title', 'id'])
class DocumentDto {
    Integer id
    String path
    String title
    Integer siteId
    Integer fileSize
    MultipartFile document
    String created
    String lastUpdated
    String createdByName
    String modifiedByName
}
