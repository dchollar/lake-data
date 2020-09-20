package com.lake.repository

import com.lake.entity.Unit
import org.springframework.data.jpa.repository.JpaRepository

interface UnitRepository extends JpaRepository<Unit, Integer> {
}
