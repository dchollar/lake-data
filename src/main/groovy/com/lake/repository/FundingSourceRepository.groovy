package com.lake.repository

import com.lake.entity.FundingSource
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository

@CompileStatic
interface FundingSourceRepository extends JpaRepository<FundingSource, Integer> {
}
