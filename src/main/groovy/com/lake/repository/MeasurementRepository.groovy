package com.lake.repository

import com.lake.entity.CharacteristicLocation
import com.lake.entity.Measurement
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface MeasurementRepository extends JpaRepository<Measurement, Integer> {
    List<Measurement> findAllByCharacteristicLocationAndCollectionDateBetween(CharacteristicLocation characteristicLocation, LocalDate fromDate, LocalDate toDate)

    List<Measurement> findAllByCharacteristicLocationInAndCollectionDate(List<CharacteristicLocation> characteristicLocations, LocalDate collectionDate)

    List<Measurement> findAllByCharacteristicLocationIn(List<CharacteristicLocation> characteristicLocations)

    List<Measurement> findAllByCollectionDate(LocalDate collectionDate)
}
