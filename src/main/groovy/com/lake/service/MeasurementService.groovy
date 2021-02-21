package com.lake.service

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.entity.*
import com.lake.repository.CharacteristicLocationRepository
import com.lake.repository.EventRepository
import com.lake.repository.MeasurementRepository
import com.lake.util.ConverterUtil
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.Normalizer
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
    CharacteristicLocationRepository characteristicLocationRepository
    @Autowired
    CharacteristicService characteristicService
    @Autowired
    LocationService locationService

    Collection<MeasurementDto> doSearch(final Integer siteId,
                                        final Integer characteristicId,
                                        final Integer locationId,
                                        final LocalDate fromDateRequest,
                                        final LocalDate toDateRequest) {
        Site site = siteService.getOne(siteId)
        Characteristic characteristic = characteristicService.getOne(characteristicId)
        LocalDate fromDate = fromDateRequest ?: MIN_DATE
        LocalDate toDate = toDateRequest ?: MAX_DATE
        if (characteristic.type == CharacteristicType.EVENT) {
            return ConverterUtil.convertEvents(eventRepository.findAllBySiteAndCharacteristicAndValueBetween(site, characteristic, fromDate, toDate))
        } else {
            Location location = locationService.getOne(locationId)
            CharacteristicLocation characteristicLocation = characteristicLocationRepository.findByCharacteristicAndLocation(characteristic, location)
            return ConverterUtil.convertMeasurements(measurementRepository.findAllByCharacteristicLocationAndCollectionDateBetween(characteristicLocation, fromDate, toDate))
        }
    }

    @Transactional
    void save(SavedMeasurementDto dto, Reporter reporter = null) {
        Characteristic characteristic = characteristicService.getOne(dto.characteristicId)
        if (characteristic.type == CharacteristicType.EVENT) {
            saveEvent(new Event(), dto, characteristic, reporter)
        } else {
            saveMeasurement(new Measurement(), dto, characteristic, reporter)
        }
    }

    @Secured('ROLE_ADMIN')
    Collection<SavedMeasurementDto> getAll(SavedMeasurementDto filter) {
        Site site = siteService.getOne(filter.siteId)
        Characteristic characteristic = characteristicService.getOne(filter.characteristicId)
        Location location = locationService.getOne(filter.locationId)

        Collection<SavedMeasurementDto> results = [] as TreeSet
        getAllMeasurements(results, site, characteristic, location, filter.collectionDate)
        if (!filter.locationId) {
            getAllEvents(results, site, characteristic, filter.collectionDate)
        }

        return results
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    SavedMeasurementDto update(Integer id, SavedMeasurementDto dto) {
        Characteristic characteristic = characteristicService.getOne(dto.characteristicId)
        if (dto.locationId) {
            saveMeasurement(measurementRepository.getOne(id), dto, characteristic)
        } else {
            saveEvent(eventRepository.getOne(id), dto, characteristic)
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

    private void getAllEvents(Collection<SavedMeasurementDto> results, Site site, Characteristic characteristic, LocalDate collectionDate) {
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

        results.addAll(ConverterUtil.convertSavedEvents(events))
    }

    private void getAllMeasurements(Collection<SavedMeasurementDto> results, Site site, Characteristic characteristic, Location location, LocalDate collectionDate) {
        List<CharacteristicLocation> characteristicLocations = getCharacteristicLocations(characteristic, location, site)

        List<Measurement> measurements = []
        if (!site && !characteristic && !location && !collectionDate) {
            measurements.addAll(measurementRepository.findAll())
        } else if (characteristicLocations && collectionDate) {
            measurements.addAll(measurementRepository.findAllByCharacteristicLocationInAndCollectionDate(characteristicLocations, collectionDate))
        } else if (characteristicLocations) {
            measurements.addAll(measurementRepository.findAllByCharacteristicLocationIn(characteristicLocations))
        } else if (collectionDate) {
            measurements.addAll(measurementRepository.findAllByCollectionDate(collectionDate))
        }

        results.addAll(ConverterUtil.convertSavedMeasurements(measurements))
    }

    private List<CharacteristicLocation> getCharacteristicLocations(Characteristic characteristic, Location location, Site site) {
        List<CharacteristicLocation> characteristicLocations = []
        if (site && location && location.site.id != site.id) {
            characteristicLocations = []
        } else if (characteristic && location) {
            characteristicLocations.add(characteristicLocationRepository.findByCharacteristicAndLocation(characteristic, location))
        } else if (characteristic) {
            List<CharacteristicLocation> temp = characteristicLocationRepository.findByCharacteristic(characteristic)
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
            characteristicLocations.addAll(characteristicLocationRepository.findByLocation(location))
        } else if (site) {
            characteristicLocations.addAll(characteristicLocationRepository.findByLocationIn(site.locations))
        }
        characteristicLocations
    }

    private void saveMeasurement(Measurement measurement, SavedMeasurementDto dto, Characteristic characteristic, Reporter reporter = null) {
        Location location = locationService.getOne(dto.locationId)
        measurement.comment = cleanComment(dto.comment)
        measurement.value = dto.value
        measurement.collectionDate = dto.collectionDate
        measurement.depth = dto.depth == null ? -1 : dto.depth
        measurement.characteristicLocation = getCharacteristicLocation(characteristic, location)
        measurement.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())
        measurementRepository.save(measurement)
    }

    private void saveEvent(Event event, SavedMeasurementDto dto, Characteristic characteristic, Reporter reporter = null) {
        event.value = dto.collectionDate
        event.comment = cleanComment(dto.comment)
        event.site = siteService.getOne(dto.siteId)
        event.year = dto.collectionDate.year
        event.characteristic = characteristic
        event.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())
        eventRepository.save(event)
        if (!characteristicService.getCharacteristicsBySite(dto.siteId).contains(ConverterUtil.convert(characteristic))) {
            characteristicService.clearCache()
        }
    }

    private static String cleanComment(String comment) {
        String s = StringUtils.stripToNull(comment)
        if (s) {
            return Normalizer.normalize(s, Normalizer.Form.NFKD).replaceAll('[^ -~]', '')
        }
        return s
    }

    private CharacteristicLocation getCharacteristicLocation(Characteristic characteristic, Location location) {
        CharacteristicLocation ul = characteristicLocationRepository.findByCharacteristicAndLocation(characteristic, location)
        if (ul) {
            return ul
        } else {
            CharacteristicLocation newUL = new CharacteristicLocation(characteristic: characteristic, location: location)
            CharacteristicLocation fromDb = characteristicLocationRepository.saveAndFlush(newUL)
            locationService.clearCache()
            characteristicService.clearCache()
            return fromDb
        }
    }
}
