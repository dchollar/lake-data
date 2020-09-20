package com.lake.repository

import com.lake.entity.Reporter
import org.springframework.data.jpa.repository.JpaRepository

interface ReporterRepository extends JpaRepository<Reporter, Integer> {
}
