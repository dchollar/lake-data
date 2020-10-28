package com.lake.util

import com.lake.dto.*
import com.lake.entity.*
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.StringUtils

import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Slf4j
class ConverterUtil {

    static Collection<AuditDto> convertAudits(Collection<Audit> entities, String timezone, AuditDto filter) {
        Set<AuditDto> dtos = new TreeSet<>()
        entities.each {
            AuditDto dto = convert(it, timezone)
            if ( (!filter.reporterName || (filter.reporterName && dto.reporterName.contains(filter.reporterName)))
                    && (!filter.controller || (filter.controller && dto.controller.contains(filter.controller)))
                    && (!filter.endpoint || (filter.endpoint && dto.endpoint.contains(filter.endpoint)))
                    && (!filter.httpMethod || (filter.httpMethod && dto.httpMethod.contains(filter.httpMethod)))) {
                dtos.add(dto)
            }
        }
        return dtos
    }

    static Collection<MeasurementDto> convertEvents(Collection<Event> entities) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<LocationDto> convertLocations(Collection<Location> entities) {
        Set<LocationDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<MeasurementDto> convertMeasurements(Collection<Measurement> entities) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        entities.each {
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

    static Set<SiteDto> convertSites(Collection<Site> entities) {
        Set<SiteDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<UnitDto> convertUnits(Collection<Unit> entities) {
        Set<UnitDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    //----------------------------------------------------------------------------------

    static AuditDto convert(Audit entity, String timezone) {
        AuditDto dto = new AuditDto()
        dto.id = entity.id
        dto.httpMethod = entity.httpMethod
        dto.endpoint = entity.endpoint
        dto.controller = entity.controller
        if (entity.reporter) {
            dto.reporterName = entity.reporter.firstName + ' ' + entity.reporter.lastName
        } else {
            dto.reporterName = ''
        }
        //  America/Chicago

        ZonedDateTime utcTime = entity.created.atZone(ZoneOffset.UTC)
        dto.created = utcTime.withZoneSameInstant(ZoneId.of(timezone))
        return dto
    }

    static MeasurementDto convert(Event entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.value
        dto.dayOfYear = entity.value.dayOfYear
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.unit = convert(entity.unit)
        dto.reporter = convert(entity.reporter)
        return dto
    }

    static LocationDto convert(Location entity) {
        LocationDto dto = new LocationDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.siteId = entity.site.id
        dto.siteDescription = entity.site.description
        dto.comment = entity.comment
        entity.unitLocations.each {
            dto.units.add(convert(it.unit))
        }
        return dto
    }

    static MeasurementDto convert(Measurement entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.collectionDate
        dto.dayOfYear = entity.collectionDate.dayOfYear
        dto.value = entity.value
        dto.depth = entity.depth
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.unit = convert(entity.unitLocation.unit)
        dto.location = convert(entity.unitLocation.location)
        dto.reporter = convert(entity.reporter)
        return dto
    }

    static ReporterDto convert(Reporter entity) {
        ReporterDto dto = new ReporterDto()
        dto.id = entity.id
        dto.firstName = entity.firstName
        dto.lastName = entity.lastName
        return dto
    }

    static ReporterDto convertForMaintenance(Reporter entity) {
        ReporterDto dto = new ReporterDto()
        dto.id = entity.id
        dto.firstName = entity.firstName
        dto.lastName = entity.lastName
        dto.emailAddress = entity.emailAddress
        dto.username = entity.username
        dto.password = ''
        dto.enabled = entity.enabled
        dto.roleAdmin = entity.roles.any { it.role == RoleType.ROLE_ADMIN }
        dto.rolePowerUser = entity.roles.any { it.role == RoleType.ROLE_POWER_USER }
        dto.roleReporter = entity.roles.any { it.role == RoleType.ROLE_REPORTER }
        return dto
    }

    static SiteDto convert(Site entity) {
        SiteDto dto = new SiteDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.waterBodyNumber = entity.waterBodyNumber
        dto.dnrRegion = entity.dnrRegion
        dto.geoRegion = entity.geoRegion
        return dto
    }

    static UnitDto convert(Unit entity) {
        UnitDto dto = new UnitDto()
        dto.id = entity.id
        dto.unitDescription = entity.unitDescription
        dto.longDescription = entity.longDescription
        dto.shortDescription = entity.shortDescription
        dto.enableDepth = entity.enableDepth
        dto.type = entity.type
        return dto
    }

    //----------------------------------------------------------------------------------

    static Location convert(LocationDto dto, Location entity) {
        entity.id = dto.id
        entity.description = StringUtils.stripToNull(dto.description)
        entity.comment = StringUtils.stripToNull(dto.comment)
        return entity
    }

    static Reporter convert(ReporterDto dto, Reporter entity) {
        entity.id = dto.id
        entity.firstName = StringUtils.stripToNull(dto.firstName)
        entity.lastName = StringUtils.stripToNull(dto.lastName)
        entity.emailAddress = StringUtils.stripToNull(dto.emailAddress)
        entity.username = StringUtils.stripToNull(dto.username)
        entity.enabled = dto.enabled

        // only mess with roles for a new reporter
        if (entity.id == null) {
            entity.roles = []
            if (dto.roleReporter) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_REPORTER))
            }
            if (dto.rolePowerUser) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_POWER_USER))
            }
            if (dto.roleAdmin) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_ADMIN))
            }
        }

        return entity
    }

    static Unit convert(UnitDto dto, Unit entity) {
        entity.id = dto.id
        entity.unitDescription = StringUtils.stripToNull(dto.unitDescription)
        entity.longDescription = StringUtils.stripToNull(dto.longDescription)
        entity.shortDescription = StringUtils.stripToNull(dto.shortDescription)
        entity.enableDepth = dto.enableDepth
        entity.type = dto.type
        return entity
    }

}
