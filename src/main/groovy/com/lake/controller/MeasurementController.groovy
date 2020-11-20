package com.lake.controller

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.dto.UnitDto
import com.lake.entity.UnitType
import com.lake.service.*
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import javax.xml.bind.ValidationException
import java.time.LocalDate

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class MeasurementController {

    @Autowired
    SiteService siteService
    @Autowired
    LocationService locationService
    @Autowired
    UnitService unitService
    @Autowired
    MeasurementService measurementService
    @Autowired
    AuditService auditService


    @GetMapping(value = '/public/api/measurements')
    Collection<MeasurementDto> getMeasurement(@RequestParam(name = 'siteId', required = true) Integer siteId,
                                              @RequestParam(name = 'unitId', required = true) Integer unitId,
                                              @RequestParam(name = 'locationId', required = false) Integer locationId,
                                              @RequestParam(name = 'fromDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                              @RequestParam(name = 'toDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        auditService.audit(HttpMethod.GET, '/public/api/measurements', this.class.simpleName)
        List valid = isValid(siteId, unitId, locationId, fromDate, toDate)
        if (valid[0]) {
            return measurementService.doSearch(siteId, unitId, locationId, fromDate, toDate)
        } else {
            throw new ValidationException(valid[1] as String)
        }
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/measurements', produces = APPLICATION_JSON_VALUE)
    Collection<SavedMeasurementDto> getAll(@RequestParam(name = 'siteId', required = false) Integer siteId,
                                           @RequestParam(name = 'unitId', required = false) Integer unitId,
                                           @RequestParam(name = 'locationId', required = false) Integer locationId,
                                           @RequestParam(name = 'collectionDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate collectionDate) {
        auditService.audit(HttpMethod.GET, '/api/measurements', this.class.simpleName)
        SavedMeasurementDto filter = new SavedMeasurementDto()
        filter.siteId = siteId == -1 ? null : siteId
        filter.unitId = unitId == -1 ? null : unitId
        filter.locationId = locationId == -1 ? null : locationId
        filter.collectionDate = collectionDate
        return measurementService.getAll(filter)
    }

    @Secured('ROLE_REPORTER')
    @PostMapping(value = '/api/measurements')
    void save(@RequestBody SavedMeasurementDto dto) {
        auditService.audit(HttpMethod.POST, '/api/measurements', this.class.simpleName)
        isValidForChange(dto)
        measurementService.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/measurements/{measurementId}', produces = APPLICATION_JSON_VALUE)
    SavedMeasurementDto update(@PathVariable(name = 'measurementId', required = true) Integer measurementId,
                               @RequestBody SavedMeasurementDto dto) {
        auditService.audit(HttpMethod.PUT, "/api/measurements/${measurementId}", this.class.simpleName)
        isValidForChange(dto)
        measurementService.update(measurementId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/measurements/{measurementId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'measurementId', required = true) Integer measurementId,
                @RequestParam(name = 'unitType', required = true) UnitType unitType) {
        auditService.audit(HttpMethod.DELETE, "/api/measurements/${measurementId}?unitType=${unitType}", this.class.simpleName)
        measurementService.delete(measurementId, unitType)
    }

    private void isValidForChange(SavedMeasurementDto dto) {
        List valid = isValid(dto.siteId, dto.unitId, dto.locationId, null, null)
        String errorMessage = valid[1] as String
        if (!dto.collectionDate) {
            errorMessage += 'Collection Date is missing '
        }
        if (errorMessage) {
            throw new ValidationException(errorMessage)
        }
    }

    private List isValid(Integer siteId, Integer unitId, Integer locationId, LocalDate fromDate, LocalDate toDate) {
        String message = ''
        if (!unitId) {
            message += 'Unit is missing '
            return [false, message]
        }
        boolean valid = true
        UnitDto unitDto = unitService.getById(unitId)
        if (!siteId) {
            message += 'site is missing '
            valid = false
        } else if (fromDate && toDate && fromDate.isAfter(toDate)) {
            message += 'from date is after to date '
            valid = false
        } else if (unitDto.type == UnitType.EVENT && locationId) {
            message += 'Not supposed to have a location '
            valid = false
        } else if (unitDto.type != UnitType.EVENT && !locationId) {
            message += 'location is missing '
            valid = false
        }
        return [valid, message]
    }

}
