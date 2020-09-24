package com.lake.repository

import com.lake.entity.Location
import com.lake.entity.Measurement
import com.lake.entity.Unit
import com.lake.entity.UnitLocation
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
    List<Measurement> findAllByUnitLocationAndCollectionDateBetween(UnitLocation unitLocation, LocalDate fromDate, LocalDate toDate)
}
