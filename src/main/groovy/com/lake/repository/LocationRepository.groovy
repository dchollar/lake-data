package com.lake.repository

import com.lake.entity.Location
import com.lake.entity.Site
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LocationRepository extends JpaRepository<Location, Integer> {

    Location findBySiteAndDescriptionIgnoreCase(Site site, String description)
}
