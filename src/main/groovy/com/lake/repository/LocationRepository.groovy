package com.lake.repository

import com.lake.entity.Location
import org.springframework.data.jpa.repository.JpaRepository

interface LocationRepository extends JpaRepository<Location, Integer> {
}
