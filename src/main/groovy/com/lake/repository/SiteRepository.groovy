package com.lake.repository

import com.lake.entity.Site
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SiteRepository extends JpaRepository<Site, Integer> {

    @Query("select distinct d.site from Document d")
    Set<Site> findAllSitesWithDocuments()
}
