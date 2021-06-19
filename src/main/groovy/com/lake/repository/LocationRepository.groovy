package com.lake.repository

import com.lake.entity.Location
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface LocationRepository extends JpaRepository<Location, Integer> {
}
