package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.entity.*
import com.lake.repository.EventRepository
import com.lake.repository.MeasurementRepository
import com.lake.repository.UnitLocationRepository
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
    SiteService siteService
    @Autowired
    MeasurementRepository measurementRepository
    @Autowired
    EventRepository eventRepository
    @Autowired
    ReporterService reporterService
    @Autowired
    UnitLocationRepository unitLocationRepository
    @Autowired
    UnitService unitService
    @Autowired
    LocationService locationService


    Collection<MeasurementDto> doSearch(final Integer siteId,
                                        final Integer unitId,
                                        final Integer locationId,
                                        final LocalDate fromDateRequest,
                                        final LocalDate toDateRequest) {
        Site site = siteService.getOne(siteId)
        Unit unit = unitService.getOne(unitId)
        LocalDate fromDate = fromDateRequest ?: MIN_DATE
        LocalDate toDate = toDateRequest ?: MAX_DATE
        log.info("unitType=${unit.type} unitId=${unit.id} siteId=${site.id} fromDate=${fromDate} toDate=${toDate}")
        if (unit.type == UnitType.EVENT) {
            return ConverterUtil.convertEvents(eventRepository.findAllBySiteAndUnitAndValueBetween(site, unit, fromDate, toDate))
        } else {
            Location location = locationService.getOne(locationId)
            UnitLocation unitLocation = unitLocationRepository.findByUnitAndLocation(unit, location)
            return ConverterUtil.convertMeasurements(measurementRepository.findAllByUnitLocationAndCollectionDateBetween(unitLocation, fromDate, toDate))
        }
    }

    @Secured('ROLE_REPORTER')
    @Transactional
    void save(SavedMeasurementDto dto) {
        Unit unit = unitService.getOne(dto.unitId)
        if (dto.locationId) {
            saveMeasurement(new Measurement(), dto, unit)
        } else {
            saveEvent(new Event(), dto, unit)
        }
    }

    @Secured('ROLE_ADMIN')
    Collection<SavedMeasurementDto> getAll(SavedMeasurementDto filter) {
        Site site = siteService.getOne(filter.siteId)
        Unit unit = unitService.getOne(filter.unitId)
        Location location = locationService.getOne(filter.locationId)

        Collection<SavedMeasurementDto> results = [] as TreeSet
        getAllMeasurements(results, site, unit, location, filter.collectionDate)
        if (!filter.locationId) {
            getAllEvents(results, site, unit, filter.collectionDate)
        }

        return results
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    SavedMeasurementDto update(Integer id, SavedMeasurementDto dto) {
        Unit unit = unitService.getOne(dto.unitId)
        if (dto.locationId) {
            saveMeasurement(measurementRepository.getOne(id), dto, unit)
        } else {
            saveEvent(eventRepository.getOne(id), dto, unit)
        }
        return dto
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void delete(Integer id, UnitType unitType) {
        if (unitType == UnitType.EVENT) {
            eventRepository.deleteById(id)
        } else {
            measurementRepository.deleteById(id)
        }
    }

    private void getAllEvents(Collection<SavedMeasurementDto> results, Site site, Unit unit, LocalDate collectionDate) {
        List<Event> events = []
        if (!site && !unit && !collectionDate) {
            events.addAll(eventRepository.findAll())
        } else if (site && unit && collectionDate) {
            events.addAll(eventRepository.findAllBySiteAndUnitAndValue(site, unit, collectionDate))
        } else if (site && unit) {
            events.addAll(eventRepository.findAllBySiteAndUnit(site, unit))
        } else if (site && collectionDate) {
            events.addAll(eventRepository.findAllBySiteAndValue(site, collectionDate))
        } else if (site) {
            events.addAll(eventRepository.findAllBySite(site))
        } else if (unit && collectionDate) {
            events.addAll(eventRepository.findAllByUnitAndValue(unit, collectionDate))
        } else if (unit) {
            events.addAll(eventRepository.findAllByUnit(unit))
        } else if (collectionDate) {
            events.addAll(eventRepository.findAllByValue(collectionDate))
        }

        results.addAll(ConverterUtil.convertSavedEvents(events))
    }

    private void getAllMeasurements(Collection<SavedMeasurementDto> results, Site site, Unit unit, Location location, LocalDate collectionDate) {
        List<UnitLocation> unitLocations = getUnitLocations(unit, location, site)

        List<Measurement> measurements = []
        if (!site && !unit && !location && !collectionDate) {
            measurements.addAll(measurementRepository.findAll())
        } else if (unitLocations && collectionDate) {
            measurements.addAll(measurementRepository.findAllByUnitLocationInAndCollectionDate(unitLocations, collectionDate))
        } else if (unitLocations) {
            measurements.addAll(measurementRepository.findAllByUnitLocationIn(unitLocations))
        } else if (collectionDate) {
            measurements.addAll(measurementRepository.findAllByCollectionDate(collectionDate))
        }

        results.addAll(ConverterUtil.convertSavedMeasurements(measurements))
    }

    private List<UnitLocation> getUnitLocations(Unit unit, Location location, Site site) {
        List<UnitLocation> unitLocations = []
        if (site && location && location.site.id != site.id) {
            unitLocations = []
        } else if (unit && location) {
            unitLocations.add(unitLocationRepository.findByUnitAndLocation(unit, location))
        } else if (unit) {
            List<UnitLocation> temp = unitLocationRepository.findByUnit(unit)
            if (site) {
                temp.each {
                    if (it.location.site.id == site.id) {
                        unitLocations.add(it)
                    }
                }
            } else {
                unitLocations.addAll(temp)
            }
        } else if (location) {
            unitLocations.addAll(unitLocationRepository.findByLocation(location))
        } else if (site) {
            unitLocations.addAll(unitLocationRepository.findByLocationIn(site.locations))
        }
        unitLocations
    }

    private void saveMeasurement(Measurement measurement, SavedMeasurementDto dto, Unit unit) {
        Location location = locationService.getOne(dto.locationId)
        measurement.comment = StringUtils.stripToNull(dto.comment)
        measurement.value = dto.value
        measurement.collectionDate = dto.collectionDate
        measurement.depth = dto.depth ?: -1
        measurement.unitLocation = getUnitLocation(unit, location)
        measurement.reporter = reporterService.reporter
        measurementRepository.save(measurement)
    }

    private void saveEvent(Event event, SavedMeasurementDto dto, Unit unit) {
        event.value = dto.collectionDate
        event.comment = StringUtils.stripToNull(dto.comment)
        event.site = siteService.getOne(dto.siteId)
        event.year = dto.collectionDate.year
        event.unit = unit
        event.reporter = reporterService.reporter
        eventRepository.save(event)
    }

    private UnitLocation getUnitLocation(Unit unit, Location location) {
        UnitLocation ul = unitLocationRepository.findByUnitAndLocation(unit, location)
        if (ul) {
            return ul
        } else {
            UnitLocation newUL = new UnitLocation(unit: unit, location: location)
            UnitLocation fromDb = unitLocationRepository.saveAndFlush(newUL)
            locationService.clearCache()
            unitService.clearCache()
            return fromDb
        }
    }
}
