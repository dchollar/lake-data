package com.lake.repository

import com.lake.entity.FundingSource
import org.springframework.data.jpa.repository.JpaRepository

interface FundingSourceRepository extends JpaRepository<FundingSource, Integer> {
}
