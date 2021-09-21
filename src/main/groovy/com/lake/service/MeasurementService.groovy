package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.*
import com.lake.repository.EventRepository
import com.lake.repository.MeasurementRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.LocalDate

@CompileStatic
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
    @Autowired
    FundingSourceService fundingSourceService

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
            return results.findAll { it?.fundingSource?.id == fundingSourceId }
        }

        return results
    }

    @Transactional
    void save(MeasurementMaintenanceDto dto) {
        Characteristic characteristic = characteristicService.getOne(dto.characteristicId)
        if (characteristic.type == CharacteristicType.EVENT) {
            saveEvent(new Event(), dto, characteristic)
        } else {
            saveMeasurement(new Measurement(), dto, characteristic)
        }
    }

    @Secured('ROLE_ADMIN')
    Collection<MeasurementMaintenanceDto> getAll(MeasurementMaintenanceDto filter) {
        Site site = siteService.getOne(filter.siteId)
        Characteristic characteristic = characteristicService.getOne(filter.characteristicId)
        Location location = locationService.getOne(filter.locationId)
        FundingSource fundingSource = fundingSourceService.getOne(filter.fundingSourceId)

        Collection<MeasurementMaintenanceDto> results = [] as TreeSet
        getAllMeasurements(results, site, characteristic, location, filter.collectionDate, filter.createdByName, filter.modifiedByName, fundingSource)
        if (!location && !fundingSource) {
            getAllEvents(results, site, characteristic, filter.collectionDate, filter.createdByName, filter.modifiedByName,)
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

    private void getAllEvents(Collection<MeasurementMaintenanceDto> results,
                              Site site, Characteristic characteristic, LocalDate collectionDate, String createdBy, String modifiedBy) {
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

        List<Event> filteredEvents = filterEventsByCreatedBy(createdBy, filterEventsByModifiedBy(modifiedBy, events))

        results.addAll(ConverterUtil.convertSavedEvents(filteredEvents))
    }

    private void getAllMeasurements(Collection<MeasurementMaintenanceDto> results,
                                    Site site, Characteristic characteristic,
                                    Location location, LocalDate collectionDate,
                                    String createdBy, String modifiedBy,
                                    FundingSource fundingSource) {
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

        List<Measurement> filteredMeasurements = filterByFundingSource(fundingSource, filterByCreatedBy(createdBy, filterByModifiedBy(modifiedBy, measurements)))

        results.addAll(ConverterUtil.convertSavedMeasurements(filteredMeasurements))
    }

    private static List<Measurement> filterByCreatedBy(String reporterName, List<Measurement> measurements) {
        if (reporterName) {
            return measurements.findAll { Measurement measurement -> "${measurement.createdBy.firstName} ${measurement.createdBy.lastName}".contains(reporterName) }
        } else {
            return measurements
        }
    }

    private static List<Measurement> filterByModifiedBy(String reporterName, List<Measurement> measurements) {
        if (reporterName) {
            return measurements.findAll { Measurement measurement -> "${measurement.modifiedBy.firstName} ${measurement.modifiedBy.lastName}".contains(reporterName) }
        } else {
            return measurements
        }
    }

    private static List<Event> filterEventsByCreatedBy(String reporterName, List<Event> events) {
        if (reporterName) {
            return events.findAll { Event event -> "${event.createdBy.firstName} ${event.createdBy.lastName}".contains(reporterName) }
        } else {
            return events
        }
    }

    private static List<Event> filterEventsByModifiedBy(String reporterName, List<Event> events) {
        if (reporterName) {
            return events.findAll { Event event -> "${event.modifiedBy.firstName} ${event.modifiedBy.lastName}".contains(reporterName) }
        } else {
            return events
        }
    }

    private static List<Measurement> filterByFundingSource(FundingSource fundingSource, List<Measurement> measurements) {
        if (fundingSource) {
            return measurements.findAll { Measurement measurement -> fundingSource.id == measurement?.fundingSource?.id }
        } else {
            return measurements
        }
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

    private void saveMeasurement(Measurement measurement, MeasurementMaintenanceDto dto, Characteristic characteristic) {
        Location location = locationService.getOne(dto.locationId)
        measurement.comment = ConverterUtil.stripNonAscii(dto.comment)
        measurement.value = dto.value
        measurement.collectionDate = dto.collectionDate
        measurement.depth = dto.depth == null ? -1 : dto.depth
        measurement.characteristicLocation = characteristicLocationService.get(characteristic, location)
        if (measurement.characteristicLocation == null) {
            throw new RuntimeException("Must define the location (site=${location.site.description} name=${location.description}) characteristic (name=${characteristic.description}) in the maintenance section before entering a measurement for it.")
        }
        measurement.fundingSource = fundingSourceService.getOne(dto.fundingSourceId)
        measurementRepository.save(measurement)
    }

    private void saveEvent(Event event, MeasurementMaintenanceDto dto, Characteristic characteristic) {
        event.value = dto.collectionDate
        event.comment = ConverterUtil.stripNonAscii(dto.comment)
        event.site = siteService.getOne(dto.siteId)
        event.year = dto.collectionDate.year
        event.characteristic = characteristic
        eventRepository.save(event)
        if (!characteristicService.getCharacteristicsBySite(dto.siteId).contains(ConverterUtil.convert(characteristic))) {
            characteristicService.clearCache()
        }
    }

}
