package com.lake.repository

import com.lake.entity.ReporterRole
import org.springframework.data.jpa.repository.JpaRepository

interface ReporterRoleRepository extends JpaRepository<ReporterRole, Integer> {
}
