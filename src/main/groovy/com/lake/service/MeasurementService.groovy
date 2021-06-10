package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.*
import com.lake.repository.EventRepository
import com.lake.repository.MeasurementRepository
import com.lake.util.ConverterUtil
import groovy.util.logging.Slf4j
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
    CharacteristicLocationService characteristicLocationService
    @Autowired
    CharacteristicService characteristicService
    @Autowired
    LocationService locationService

    Collection<MeasurementDto> doSearch(final Integer siteId,
                                        final Integer characteristicId,
                                        final Integer locationId,
                                        final LocalDate fromDateRequest,
                                        final LocalDate toDateRequest,
                                        final Integer fundingSourceId) {
        Site site = siteService.getOne(siteId)
        Characteristic characteristic = characteristicService.getOne(characteristicId)
        LocalDate fromDate = fromDateRequest ?: MIN_DATE
        LocalDate toDate = toDateRequest ?: MAX_DATE
        Collection<MeasurementDto> results
        if (characteristic.type == CharacteristicType.EVENT) {
            results = ConverterUtil.convertEvents(eventRepository.findAllBySiteAndCharacteristicAndValueBetween(site, characteristic, fromDate, toDate))
        } else {
            Location location = locationService.getOne(locationId)
            CharacteristicLocation characteristicLocation = characteristicLocationService.get(characteristic, location)
            results = ConverterUtil.convertMeasurements(measurementRepository.findAllByCharacteristicLocationAndCollectionDateBetween(characteristicLocation, fromDate, toDate))
        }

        if (fundingSourceId) {
            return results.findAll {it?.fundingSource?.id == fundingSourceId}
        }

        return results
    }

    @Transactional
    void save(MeasurementMaintenanceDto dto, Reporter reporter = null) {
        Characteristic characteristic = characteristicService.getOne(dto.characteristicId)
        if (characteristic.type == CharacteristicType.EVENT) {
            saveEvent(new Event(), dto, characteristic, reporter)
        } else {
            saveMeasurement(new Measurement(), dto, characteristic, reporter)
        }
    }

    @Secured('ROLE_ADMIN')
    Collection<MeasurementMaintenanceDto> getAll(MeasurementMaintenanceDto filter) {
        Site site = siteService.getOne(filter.siteId)
        Characteristic characteristic = characteristicService.getOne(filter.characteristicId)
        Location location = locationService.getOne(filter.locationId)

        Collection<MeasurementMaintenanceDto> results = [] as TreeSet
        getAllMeasurements(results, site, characteristic, location, filter.collectionDate, filter.reporterName)
        if (!filter.locationId) {
            getAllEvents(results, site, characteristic, filter.collectionDate, filter.reporterName)
        }

        return results
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    MeasurementMaintenanceDto update(Integer id, MeasurementMaintenanceDto dto) {
        Characteristic characteristic = characteristicService.getOne(dto.characteristicId)
        if (dto.locationId) {
            saveMeasurement(measurementRepository.getById(id), dto, characteristic)
        } else {
            saveEvent(eventRepository.getById(id), dto, characteristic)
        }
        return dto
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void delete(Integer id, CharacteristicType characteristicType) {
        if (characteristicType == CharacteristicType.EVENT) {
            eventRepository.deleteById(id)
        } else {
            measurementRepository.deleteById(id)
        }
    }

    private void getAllEvents(Collection<MeasurementMaintenanceDto> results, Site site, Characteristic characteristic, LocalDate collectionDate, String reporterName) {
        List<Event> events = []
        if (!site && !characteristic && !collectionDate) {
            events.addAll(eventRepository.findAll())
        } else if (site && characteristic && collectionDate) {
            events.addAll(eventRepository.findAllBySiteAndCharacteristicAndValue(site, characteristic, collectionDate))
        } else if (site && characteristic) {
            events.addAll(eventRepository.findAllBySiteAndCharacteristic(site, characteristic))
        } else if (site && collectionDate) {
            events.addAll(eventRepository.findAllBySiteAndValue(site, collectionDate))
        } else if (site) {
            events.addAll(eventRepository.findAllBySite(site))
        } else if (characteristic && collectionDate) {
            events.addAll(eventRepository.findAllByCharacteristicAndValue(characteristic, collectionDate))
        } else if (characteristic) {
            events.addAll(eventRepository.findAllByCharacteristic(characteristic))
        } else if (collectionDate) {
            events.addAll(eventRepository.findAllByValue(collectionDate))
        }

        List<Event> filteredEvents
        if (reporterName) {
            filteredEvents = events.findAll { Event event -> "${event.reporter.firstName} ${event.reporter.lastName}".contains(reporterName) }
        } else {
            filteredEvents = events
        }

        results.addAll(ConverterUtil.convertSavedEvents(filteredEvents))
    }

    private void getAllMeasurements(Collection<MeasurementMaintenanceDto> results, Site site, Characteristic characteristic, Location location, LocalDate collectionDate, String reporterName) {
        List<CharacteristicLocation> characteristicLocations = getCharacteristicLocations(characteristic, location, site)

        List<Measurement> measurements = []
        if (site && location && location.site.id != site.id) {
            // do nothing. no results will be found
        } else if (!site && !characteristic && !location && !collectionDate) {
            measurements.addAll(measurementRepository.findAll())
        } else if (characteristicLocations && collectionDate) {
            measurements.addAll(measurementRepository.findAllByCharacteristicLocationInAndCollectionDate(characteristicLocations, collectionDate))
        } else if (characteristicLocations) {
            measurements.addAll(measurementRepository.findAllByCharacteristicLocationIn(characteristicLocations))
        } else if (collectionDate) {
            measurements.addAll(measurementRepository.findAllByCollectionDate(collectionDate))
        }

        List<Measurement> filteredMeasurements
        if (reporterName) {
            filteredMeasurements = measurements.findAll { Measurement measurement -> "${measurement.reporter.firstName} ${measurement.reporter.lastName}".contains(reporterName) }
        } else {
            filteredMeasurements = measurements
        }

        results.addAll(ConverterUtil.convertSavedMeasurements(filteredMeasurements))
    }

    private List<CharacteristicLocation> getCharacteristicLocations(Characteristic characteristic, Location location, Site site) {
        List<CharacteristicLocation> characteristicLocations = []
        if (site && location && location.site.id != site.id) {
            // do nothing. no results will be found
        } else if (characteristic && location) {
            characteristicLocations.add(characteristicLocationService.get(characteristic, location))
        } else if (characteristic) {
            List<CharacteristicLocation> temp = characteristicLocationService.getByCharacteristic(characteristic)
            if (site) {
                temp.each {
                    if (it.location.site.id == site.id) {
                        characteristicLocations.add(it)
                    }
                }
            } else {
                characteristicLocations.addAll(temp)
            }
        } else if (location) {
            characteristicLocations.addAll(characteristicLocationService.getByLocation(location))
        } else if (site) {
            characteristicLocations.addAll(characteristicLocationService.getBySite(site))
        }
        characteristicLocations
    }

    private void saveMeasurement(Measurement measurement, MeasurementMaintenanceDto dto, Characteristic characteristic, Reporter reporter = null) {
        Location location = locationService.getOne(dto.locationId)
        measurement.comment = ConverterUtil.stripNonAscii(dto.comment)
        measurement.value = dto.value
        measurement.collectionDate = dto.collectionDate
        measurement.depth = dto.depth == null ? -1 : dto.depth
        measurement.characteristicLocation = characteristicLocationService.get(characteristic, location)
        if (measurement.characteristicLocation == null) {
            throw new RuntimeException("Must define the location characteristic in the maintenance section before entering a measurement for it.")
        }
        measurement.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())
        measurementRepository.save(measurement)
    }

    private void saveEvent(Event event, MeasurementMaintenanceDto dto, Characteristic characteristic, Reporter reporter = null) {
        event.value = dto.collectionDate
        event.comment = ConverterUtil.stripNonAscii(dto.comment)
        event.site = siteService.getOne(dto.siteId)
        event.year = dto.collectionDate.year
        event.characteristic = characteristic
        event.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())
        eventRepository.save(event)
        if (!characteristicService.getCharacteristicsBySite(dto.siteId).contains(ConverterUtil.convert(characteristic))) {
            characteristicService.clearCache()
        }
    }

}
