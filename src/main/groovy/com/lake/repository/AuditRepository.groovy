package com.lake.repository

import com.lake.entity.Audit
import org.springframework.data.jpa.repository.JpaRepository

interface AuditRepository extends JpaRepository<Audit, Integer> {
}
