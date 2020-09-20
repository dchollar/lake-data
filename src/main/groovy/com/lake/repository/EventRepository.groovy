package com.lake.repository

import com.lake.entity.Event
import com.lake.entity.Site
import com.lake.entity.Unit
import org.springframework.data.jpa.repository.JpaRepository

import java.time.LocalDate

interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findAllBySiteAndUnitAndValueBetween(Site site, Unit unit, LocalDate fromDate, LocalDate toDate)
}
