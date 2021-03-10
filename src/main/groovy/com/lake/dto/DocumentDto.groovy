package com.lake.dto

import groovy.transform.EqualsAndHashCode
import groovy.transform.Sortable
import org.springframework.web.multipart.MultipartFile

@EqualsAndHashCode(includes = ['id'])
@Sortable(includes = ['siteId', 'path', 'title', 'id'])
class DocumentDto {
    Integer id
    String path
    String title
    Integer siteId
    Integer fileSize
    String created
    String lastUpdated
    MultipartFile document
}
