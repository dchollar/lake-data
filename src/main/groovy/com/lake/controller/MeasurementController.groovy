package com.lake.controller

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.entity.CharacteristicType
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
    MeasurementService measurementService
    @Autowired
    AuditService auditService
    @Autowired
    ValidationService validationService

    @GetMapping(value = '/public/api/measurements')
    Collection<MeasurementDto> getMeasurement(@RequestParam(name = 'siteId', required = true) Integer siteId,
                                              @RequestParam(name = 'characteristicId', required = true) Integer characteristicId,
                                              @RequestParam(name = 'locationId', required = false) Integer locationId,
                                              @RequestParam(name = 'fromDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                              @RequestParam(name = 'toDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {
        auditService.audit(HttpMethod.GET.name(), '/public/api/measurements', this.class.simpleName)
        List valid = validationService.isValid(siteId, characteristicId, locationId, fromDate, toDate)
        if (valid[0]) {
            return measurementService.doSearch(siteId, characteristicId, locationId, fromDate, toDate)
        } else {
            throw new ValidationException(valid[1] as String)
        }
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/measurements', produces = APPLICATION_JSON_VALUE)
    Collection<SavedMeasurementDto> getAll(@RequestParam(name = 'siteId', required = false) Integer siteId,
                                           @RequestParam(name = 'characteristicId', required = false) Integer characteristicId,
                                           @RequestParam(name = 'locationId', required = false) Integer locationId,
                                           @RequestParam(name = 'collectionDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate collectionDate) {
        auditService.audit(HttpMethod.GET.name(), '/api/measurements', this.class.simpleName)
        SavedMeasurementDto filter = new SavedMeasurementDto()
        filter.siteId = siteId == -1 ? null : siteId
        filter.characteristicId = characteristicId == -1 ? null : characteristicId
        filter.locationId = locationId == -1 ? null : locationId
        filter.collectionDate = collectionDate
        return measurementService.getAll(filter)
    }

    @Secured('ROLE_REPORTER')
    @PostMapping(value = '/api/measurements')
    void save(@RequestBody SavedMeasurementDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/measurements', this.class.simpleName)
        validationService.isValidForChange(dto)
        measurementService.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/measurements/{measurementId}', produces = APPLICATION_JSON_VALUE)
    SavedMeasurementDto update(@PathVariable(name = 'measurementId', required = true) Integer measurementId,
                               @RequestBody SavedMeasurementDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/measurements/${measurementId}", this.class.simpleName)
        validationService.isValidForChange(dto)
        measurementService.update(measurementId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/measurements/{measurementId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'measurementId', required = true) Integer measurementId,
                @RequestParam(name = 'characteristicType', required = true) CharacteristicType characteristicType) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/measurements/${measurementId}?characteristicType=${characteristicType}", this.class.simpleName)
        measurementService.delete(measurementId, characteristicType)
    }

}
