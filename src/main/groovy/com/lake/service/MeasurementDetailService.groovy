package com.lake.service

import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.*
import com.lake.repository.MeasurementDetailRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

import javax.transaction.Transactional
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@CompileStatic
@Slf4j
@Service
class MeasurementDetailService {

    @Autowired
    CharacteristicLocationService characteristicLocationService
    @Autowired
    CharacteristicService characteristicService
    @Autowired
    MeasurementService measurementService
    @Autowired
    MeasurementDetailRepository repository

    @Secured('ROLE_ADMIN')
    @Transactional
    void save(Location location, Integer characteristicId, Instant collectionTime, BigDecimal value, String source) {
        Characteristic characteristic = characteristicService.getOne(characteristicId)
        if (characteristic) {
            save(location, characteristic, collectionTime, value, source)
        }
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void save(Location location, Characteristic characteristic, Instant collectionTime, BigDecimal value, String source) {
        characteristicLocationService.getByCharacteristic(characteristic)
        CharacteristicLocation cl = characteristicLocationService.get(characteristic, location)
        if (cl == null) {
            cl = characteristicLocationService.save(characteristic, location)
        }
        save(cl, collectionTime, value, source)
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void save(CharacteristicLocation cl, Instant collectionTime, BigDecimal value, String source) {
        MeasurementDetail detail = new MeasurementDetail(
                characteristicLocation: cl,
                collectionTime: collectionTime,
                value: value,
                source: source,
                created: Instant.now(),
                lastUpdated: Instant.now(),
                status: StatusType.NEW)
        repository.save(detail)
    }

    @Secured('ROLE_ADMIN')
    void summarize() {
        List<MeasurementDetail> details = repository.findByStatus(StatusType.NEW)
        while (details.size() > 0) {
            MeasurementDetail detail = details.first()
            LocalDate date = detail.collectionTime.atZone(ZoneOffset.UTC).toLocalDate()
            summarizeInstance(date, detail.characteristicLocation)

            details = repository.findByStatus(StatusType.NEW)
        }
    }

    private void summarizeInstance(LocalDate date, CharacteristicLocation cl) {
        List<MeasurementDetail> details = repository.findByDateAndCharacteristicLocation(date, cl.id)
        if (details.size() > 0) {
            BigDecimal average = calculateAverageValue(details)
            saveMeasurement(date, average, cl)
            markComplete(details)
        }
    }

    private static BigDecimal calculateAverageValue(List<MeasurementDetail> details) {
        BigDecimal total = BigDecimal.ZERO
        details.each { MeasurementDetail detail ->
            total += detail.value
        }
        BigDecimal size = new BigDecimal(details.size())
        return total.divide(size, 3, RoundingMode.HALF_UP)
    }

    private void saveMeasurement(LocalDate date, BigDecimal average, CharacteristicLocation cl) {
        MeasurementMaintenanceDto dto = new MeasurementMaintenanceDto(
                collectionDate: date,
                value: average,
                locationId: cl.location.id,
                characteristicId: cl.characteristic.id,
                siteId: cl.location.site.id,
                characteristicType: CharacteristicType.WATER
        )

        try {
            measurementService.save(dto)
        } catch (Exception e) {
            // different data files will contain parts of the same day so updates are necessary
            measurementService.updateMeasurement(cl, dto)
        }
    }

    private List<MeasurementDetail> markComplete(List<MeasurementDetail> details) {
        details.each { MeasurementDetail detail ->
            detail.status = StatusType.COMPLETE
            detail.lastUpdated = Instant.now()
            repository.save(detail)
        }
    }

}
