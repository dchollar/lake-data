package com.lake.repository

import com.lake.entity.Site
import org.springframework.data.jpa.repository.JpaRepository

interface SiteRepository extends JpaRepository<Site, Integer> {
}
