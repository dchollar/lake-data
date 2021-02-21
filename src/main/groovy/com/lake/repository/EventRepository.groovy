package com.lake.repository

import com.lake.entity.Characteristic
import com.lake.entity.Event
import com.lake.entity.Site
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllBySiteAndCharacteristicAndValueBetween(Site site, Characteristic characteristic, LocalDate fromDate, LocalDate toDate)

    List<Event> findAllBySiteAndCharacteristicAndValue(Site site, Characteristic characteristic, LocalDate collectionDate)

    List<Event> findAllBySiteAndCharacteristic(Site site, Characteristic characteristic)

    List<Event> findAllByCharacteristicAndValue(Characteristic characteristic, LocalDate collectionDate)

    List<Event> findAllBySiteAndValue(Site site, LocalDate collectionDate)

    List<Event> findAllByCharacteristic(Characteristic characteristic)

    List<Event> findAllBySite(Site site)

    List<Event> findAllByValue(LocalDate collectionDate)

}
