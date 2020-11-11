package com.lake.repository

import com.lake.entity.Event
import com.lake.entity.Site
import com.lake.entity.Unit
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllBySiteAndUnitAndValueBetween(Site site, Unit unit, LocalDate fromDate, LocalDate toDate)

    List<Event> findAllBySiteAndUnitAndValue(Site site, Unit unit, LocalDate collectionDate)

    List<Event> findAllBySiteAndUnit(Site site, Unit unit)

    List<Event> findAllByUnitAndValue(Unit unit, LocalDate collectionDate)

    List<Event> findAllBySiteAndValue(Site site, LocalDate collectionDate)

    List<Event> findAllByUnit(Unit unit)

    List<Event> findAllBySite(Site site)

    List<Event> findAllByValue(LocalDate collectionDate)

}
