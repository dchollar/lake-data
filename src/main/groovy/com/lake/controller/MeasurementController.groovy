package com.lake.controller

import com.lake.dto.MeasurementDto
import com.lake.dto.SavedMeasurementDto
import com.lake.dto.UnitDto
import com.lake.entity.UnitType
import com.lake.service.MeasurementService
import com.lake.service.UnitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import javax.xml.bind.ValidationException
import java.time.LocalDate

@Slf4j
@RestController
class MeasurementController {

    @Autowired
    UnitService unitService
    @Autowired
    MeasurementService measurementService

    @Secured('ROLE_REPORTER')
    @PostMapping(value = '/api/measurements')
    void save(@RequestBody SavedMeasurementDto dto) {
        List valid = isValid(dto.siteId, dto.unitId, dto.locationId, null, null)
        String errorMessage = valid[1] as String
        if (!dto.collectionDate) {
            errorMessage += 'Collection Date is missing '
        }
        if (dto.collectionDate && valid[0]) {
            measurementService.save(dto)
        } else {
            throw new ValidationException(errorMessage)
        }
    }

    @GetMapping(value = '/public/api/measurements')
    Collection<MeasurementDto> getMeasurement(@RequestParam(name = 'siteId', required = true) Integer siteId,
                                              @RequestParam(name = 'unitId', required = true) Integer unitId,
                                              @RequestParam(name = 'locationId', required = false) Integer locationId,
                                              @RequestParam(name = 'fromDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                              @RequestParam(name = 'toDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        List valid = isValid(siteId, unitId, locationId, fromDate, toDate)
        if (valid[0]) {
            return measurementService.doSearch(siteId, unitId, locationId, fromDate, toDate)
        } else {
            throw new ValidationException(valid[1] as String)
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
        return [valid,message]
    }

}
