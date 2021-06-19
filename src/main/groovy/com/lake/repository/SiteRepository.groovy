package com.lake.repository

import com.lake.entity.Site
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@CompileStatic
interface SiteRepository extends JpaRepository<Site, Integer> {

    @Query("select distinct d.site from Document d")
    Set<Site> findAllSitesWithDocuments()
}
