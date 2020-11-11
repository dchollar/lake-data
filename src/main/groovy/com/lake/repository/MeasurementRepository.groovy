package com.lake.repository


import com.lake.entity.Measurement
import com.lake.entity.UnitLocation
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
    List<Measurement> findAllByUnitLocationAndCollectionDateBetween(UnitLocation unitLocation, LocalDate fromDate, LocalDate toDate)

    List<Measurement> findAllByUnitLocationInAndCollectionDate(List<UnitLocation> unitLocations, LocalDate collectionDate)

    List<Measurement> findAllByUnitLocationIn(List<UnitLocation> unitLocations)

    List<Measurement> findAllByCollectionDate(LocalDate collectionDate)
}
