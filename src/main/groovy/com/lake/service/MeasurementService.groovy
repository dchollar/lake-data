package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.entity.Location
import com.lake.entity.Site
import com.lake.entity.Unit
import com.lake.entity.UnitType
import com.lake.repository.*
import com.lake.util.ConverterUtil
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
    LocationRepository locationRepository

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
            Location location = locationRepository.getOne(locationId)
            return ConverterUtil.convertMeasurements(measurementRepository.findAllByLocationAndUnitAndCollectionDateBetween(location, unit, fromDate, toDate))
        }
    }
}
