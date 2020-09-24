package com.lake.util

import com.lake.dto.*
import com.lake.entity.*
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils

@Slf4j
class ConverterUtil {

    static Collection<MeasurementDto> convertEvents(Collection<Event> events) {
        log.info("The number of events is: ${events.size()}")
        Set<MeasurementDto> dtos = new TreeSet<>()
        events.each {
            dtos.add(convert(it))
        }
        log.info("The number of dtos is: ${dtos.size()}")
        return dtos
    }

    static Collection<MeasurementDto> convertMeasurements(Collection<Measurement> events) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        events.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<ReporterDto> convertReportersForMaintenance(Collection<Reporter> entities) {
        Set<ReporterDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertForMaintenance(it))
        }
        return dtos
    }

    static Set<SiteDto> convertSites(Collection<Site> sites) {
        Set<SiteDto> dtos = new TreeSet<>()
        sites.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<UnitDto> convertUnits(Collection<Unit> units) {
        Set<UnitDto> dtos = new TreeSet<>()
        units.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<LocationDto> convertLocations(Collection<Location> locations) {
        Set<LocationDto> dtos = new TreeSet<>()
        locations.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static SiteDto convert(Site site) {
        SiteDto dto = new SiteDto()
        dto.id = site.id
        dto.description = site.description
        dto.waterBodyNumber = site.waterBodyNumber
        dto.dnrRegion = site.dnrRegion
        dto.geoRegion = site.geoRegion
        return dto
    }

    static LocationDto convert(Location location) {
        LocationDto dto = new LocationDto(id: location.id, description: location.description)
        location.unitLocations.each {
            dto.units.add(convert(it.unit))
        }
        return dto
    }

    static UnitDto convert(Unit unit) {
        UnitDto dto = new UnitDto()
        dto.id = unit.id
        dto.unitDescription = unit.unitDescription
        dto.longDescription = unit.longDescription
        dto.shortDescription = unit.shortDescription
        dto.enableDepth = unit.enableDepth
        dto.type = unit.type
        return dto
    }

    static ReporterDto convert(Reporter reporter) {
        ReporterDto dto = new ReporterDto()
        dto.id = reporter.id
        dto.firstName = reporter.firstName
        dto.lastName = reporter.lastName
        //dto.emailAddress = reporter.emailAddress
        //dto.phoneNumber = reporter.phoneNumber
        //dto.username = reporter.username
        //dto.password = reporter.password
        //dto.enabled = reporter.enabled
        //dto.roles = reporter.roles.collect { it.role }
        return dto
    }

    static ReporterDto convertForMaintenance(Reporter reporter) {
        ReporterDto dto = new ReporterDto()
        dto.id = reporter.id
        dto.firstName = reporter.firstName
        dto.lastName = reporter.lastName
        dto.emailAddress = reporter.emailAddress
        dto.phoneNumber = reporter.phoneNumber
        dto.username = reporter.username
        dto.password = reporter.password //TODO need to decode
        dto.enabled = reporter.enabled
        dto.roles = reporter.roles.collect { it.role }
        return dto
    }

    static MeasurementDto convert(Measurement measurement) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = measurement.id
        dto.collectionDate = measurement.collectionDate
        dto.value = measurement.value
        dto.depth = measurement.depth
        dto.comment = StringUtils.stripToEmpty(measurement.comment)
        dto.unit = convert(measurement.unitLocation.unit)
        dto.location = convert(measurement.unitLocation.location)
        dto.reporter = convert(measurement.reporter)
        return dto
    }

    static MeasurementDto convert(Event event) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = event.id
        dto.collectionDate = event.value
        dto.comment = StringUtils.stripToEmpty(event.comment)
        dto.unit = convert(event.unit)
        dto.reporter = convert(event.reporter)
        return dto
    }

}
