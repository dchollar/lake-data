package com.lake.service

import com.lake.dto.SavedMeasurementDto
import com.lake.dto.UnitDto
import com.lake.entity.UnitType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.xml.bind.ValidationException
import java.time.LocalDate

@Service
class ValidationService {

    @Autowired
    UnitService unitService

    void isValidForChange(SavedMeasurementDto dto) {
        List valid = isValid(dto.siteId, dto.unitId, dto.locationId, null, null)
        String errorMessage = valid[1] as String
        UnitDto unitDto = unitService.getById(dto.unitId)
        if (unitDto.enableDepth && (dto.depth == null || dto.depth < 0)) {
            errorMessage += 'Depth is missing or less than zero '
        }
        if (!unitDto.enableDepth && (dto.depth != null && dto.depth != -1)) {
            errorMessage += 'Depth should be -1 '
        }
        if (unitDto.type != UnitType.EVENT && dto.value == null) {
            errorMessage += 'value is missing '
        }
        if (!dto.collectionDate) {
            errorMessage += 'Collection Date is missing '
        }
        if (errorMessage) {
            throw new ValidationException(errorMessage)
        }
    }

    List isValid(Integer siteId, Integer unitId, Integer locationId, LocalDate fromDate, LocalDate toDate) {
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
