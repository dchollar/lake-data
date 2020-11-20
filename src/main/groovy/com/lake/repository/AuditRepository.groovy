package com.lake.repository

import com.lake.entity.Audit
import org.springframework.data.jpa.repository.JpaRepository

import java.time.Instant

interface AuditRepository extends JpaRepository<Audit, Integer> {

    List<Audit> findAllByCreatedLessThan(Instant now)
}
