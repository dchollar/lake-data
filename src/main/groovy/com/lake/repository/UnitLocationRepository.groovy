package com.lake.repository

import com.lake.entity.Location
import com.lake.entity.Unit
import com.lake.entity.UnitLocation
import org.springframework.data.jpa.repository.JpaRepository

interface UnitLocationRepository extends JpaRepository<UnitLocation, Integer> {

    UnitLocation findByUnitAndLocation(Unit unit, Location location)
}
