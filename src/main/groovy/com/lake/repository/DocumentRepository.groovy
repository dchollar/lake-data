package com.lake.repository


import com.lake.entity.Document
import com.lake.entity.Site
import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository extends JpaRepository<Document, Integer> {

    List<Document> findBySite(Site site)

}
