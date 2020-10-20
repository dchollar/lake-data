package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.entity.*
import com.lake.repository.EventRepository
import com.lake.repository.MeasurementRepository
import com.lake.repository.SiteRepository
import com.lake.repository.UnitRepository
import com.lake.util.ConverterUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

@Slf4j
@Service
class MeasurementService {

    private static final LocalDate MAX_DATE = LocalDate.of(4000, 12, 31)
    private static final LocalDate MIN_DATE = LocalDate.of(1900, 01, 01)

    @Autowired
    SiteRepository siteRepository
    @Autowired
    UnitRepository unitRepository
    @Autowired
    MeasurementRepository measurementRepository
    @Autowired
    EventRepository eventRepository
    @Autowired
    ReporterService reporterService


    Collection<MeasurementDto> doSearch(final Integer siteId,
                                        final Integer unitId,
                                        final Integer locationId,
                                        final LocalDate fromDateRequest,
                                        final LocalDate toDateRequest) {
        Site site = siteRepository.getOne(siteId)
        Unit unit = unitRepository.getOne(unitId)
        LocalDate fromDate = fromDateRequest ?: MIN_DATE
        LocalDate toDate = toDateRequest ?: MAX_DATE
        log.info("unitType=${unit.type} unitId=${unit.id} siteId=${site.id} fromDate=${fromDate} toDate=${toDate}")
        if (unit.type == UnitType.EVENT) {
            log.info('Getting event measurement data')
            return ConverterUtil.convertEvents(eventRepository.findAllBySiteAndUnitAndValueBetween(site, unit, fromDate, toDate))
        } else {
            UnitLocation unitLocation = unit.unitLocations.find { it.location.id == locationId }
            return ConverterUtil.convertMeasurements(measurementRepository.findAllByUnitLocationAndCollectionDateBetween(unitLocation, fromDate, toDate))
        }
    }

    @Secured('ROLE_REPORTER')
    @Transactional
    void save(SavedMeasurementDto dto) {
        Unit unit = unitRepository.getOne(dto.unitId)
        Reporter reporter = reporterService.reporter
        if (dto.locationId) {
            // must be a measurement
            Measurement measurement = new Measurement()
            measurement.comment = StringUtils.stripToNull(dto.comment)
            measurement.value = dto.value
            measurement.collectionDate = dto.collectionDate
            measurement.depth = dto.depth ?: -1
            measurement.unitLocation = unit.unitLocations.find { it.location.id == dto.locationId }
            measurement.reporter = reporter
            measurementRepository.save(measurement)
        } else {
            // must be an event
            Event event = new Event()
            event.value = dto.collectionDate
            event.comment = StringUtils.stripToNull(dto.comment)
            event.site = siteRepository.getOne(dto.siteId)
            event.year = dto.collectionDate.year
            event.unit = unit
            event.reporter = reporter
            eventRepository.save(event)
        }
    }
}
