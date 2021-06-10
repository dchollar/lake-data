package com.lake.service

import com.lake.dto.CharacteristicDto
import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.CharacteristicType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import javax.xml.bind.ValidationException
import java.time.LocalDate

@Service
class ValidationService {

    @Autowired
    CharacteristicService service

    void isValidForChange(MeasurementMaintenanceDto dto) {
        List valid = isValid(dto.siteId, dto.characteristicId, dto.locationId, null, null)
        String errorMessage = valid[1] as String
        CharacteristicDto characteristicDto = service.getById(dto.characteristicId)
        if (characteristicDto.enableDepth && (dto.depth == null || dto.depth < 0)) {
            errorMessage += 'Depth is missing or less than zero '
        }
        if (!characteristicDto.enableDepth && (dto.depth != null && dto.depth != -1)) {
            errorMessage += 'Depth should be -1 '
        }
        if (characteristicDto.type != CharacteristicType.EVENT && dto.value == null) {
            errorMessage += 'value is missing '
        }
        if (!dto.collectionDate) {
            errorMessage += 'Collection Date is missing '
        }
        if (errorMessage) {
            throw new ValidationException(errorMessage)
        }
    }

    List isValid(Integer siteId, Integer characteristicId, Integer locationId, LocalDate fromDate, LocalDate toDate) {
        String message = ''
        if (!characteristicId) {
            message += 'characteristic is missing '
            return [false, message]
        }
        boolean valid = true
        CharacteristicDto characteristicDto = service.getById(characteristicId)
        if (!siteId) {
            message += 'site is missing '
            valid = false
        } else if (fromDate && toDate && fromDate.isAfter(toDate)) {
            message += 'from date is after to date '
            valid = false
        } else if (characteristicDto.type == CharacteristicType.EVENT && locationId) {
            message += 'Not supposed to have a location '
            valid = false
        } else if (characteristicDto.type != CharacteristicType.EVENT && !locationId) {
            message += 'location is missing '
            valid = false
        }
        return [valid, message]
    }

}
