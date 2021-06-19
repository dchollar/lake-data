package com.lake.repository


import com.lake.entity.Document
import com.lake.entity.Site
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findBySite(Site site)

    List<Document> findBySiteAndTextContainingIgnoreCase(Site site, String word)

}
