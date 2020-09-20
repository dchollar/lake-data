package com.lake.controller

import com.lake.dto.MeasurementDto
import com.lake.dto.UnitDto
import com.lake.entity.UnitType
import com.lake.service.MeasurementService
import com.lake.service.UnitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.xml.bind.ValidationException
import java.time.LocalDate

@Slf4j
@RestController
class MeasurementController {

    @Autowired
    UnitService unitService
    @Autowired
    MeasurementService measurementService

    @GetMapping(value = '/public/api/measurements')
    Collection<MeasurementDto> getMeasurement(@RequestParam(name = 'siteId', required = true) Integer siteId,
                                              @RequestParam(name = 'unitId', required = true) Integer unitId,
                                              @RequestParam(name = 'locationId', required = false) Integer locationId,
                                              @RequestParam(name = 'fromDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                                              @RequestParam(name = 'toDate', required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

        if (isValid(siteId, unitId, locationId, fromDate, toDate)) {
            Collection<MeasurementDto> results = measurementService.doSearch(siteId, unitId, locationId, fromDate, toDate)
            log.info("Got this many results back: ${results.size()}")
            return results
        } else {
            throw new ValidationException("Invalid choices")
        }
    }

    private boolean isValid(Integer siteId, Integer unitId, Integer locationId, LocalDate fromDate, LocalDate toDate) {
        boolean valid = true
        UnitDto unitDto = unitService.getById(unitId)
        if (!siteId) {
            valid = false
        } else if (fromDate && toDate && fromDate.isAfter(toDate)) {
            valid = false
        } else if (unitDto.type == UnitType.EVENT && locationId) {
            valid = false
        } else if (unitDto.type != UnitType.EVENT && !locationId) {
            valid = false
        }
        return valid
    }

}
