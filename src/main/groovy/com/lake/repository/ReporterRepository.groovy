package com.lake.repository

import com.lake.entity.Reporter
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface ReporterRepository extends JpaRepository<Reporter, Integer> {

    Reporter findByUsername(String username)
}
