package com.lake.service

import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicType
import groovy.transform.CompileStatic
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDate

@CompileStatic
@Service
class ValidationService {

    @Autowired
    CharacteristicService service

    void isValidForChange(MeasurementMaintenanceDto dto) {
        isValid(dto.siteId, dto.characteristicId, dto.locationId, null, null)

        String errorMessage = ''
        Characteristic characteristic = service.getOne(dto.characteristicId)
        if (characteristic.enableDepth && (dto.depth == null || dto.depth < 0)) {
            errorMessage += 'Depth is missing or less than zero '
        }
        if (!characteristic.enableDepth && (dto.depth != null && dto.depth != -1)) {
            errorMessage += 'Depth should be -1 '
        }
        if (characteristic.type != CharacteristicType.EVENT && dto.value == null) {
            errorMessage += 'value is missing '
        }
        if (!dto.collectionDate) {
            errorMessage += 'Collection Date is missing '
        }

        if (errorMessage) {
            throw new ValidationException(errorMessage)
        }
    }

    void isValid(Integer siteId, Integer characteristicId, Integer locationId, LocalDate fromDate, LocalDate toDate) {
        String message = ''
        if (!characteristicId) {
            message += 'characteristic is missing '
            throw new ValidationException(message)
        }
        Characteristic characteristic = service.getOne(characteristicId)
        if (!siteId) {
            message += 'site is missing '
        } else if (fromDate && toDate && fromDate.isAfter(toDate)) {
            message += 'from date is after to date '
        } else if (characteristic.type == CharacteristicType.EVENT && locationId) {
            message += 'Not supposed to have a location '
        } else if (characteristic.type != CharacteristicType.EVENT && !locationId) {
            message += 'location is missing '
        }
        if (message) {
            throw new ValidationException(message)
        }
    }

}
