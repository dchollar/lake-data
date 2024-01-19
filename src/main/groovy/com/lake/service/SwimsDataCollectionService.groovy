package com.lake.service

import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.CharacteristicType
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.validation.ValidationException
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@CompileStatic
@Slf4j
abstract class SwimsDataCollectionService {

    // DNR Station Ids
    protected static final NORTH_PIPE_LAKE_STATION_ID = '493105'
    protected static final PIPE_LAKE_STATION_ID = '493097'

    // site ids
    protected static final int PIPE_LAKE_SITE_ID = 1
    protected static final int NORTH_PIPE_LAKE_SITE_ID = 2

    // location ids
    protected static final int NORTH_PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID = 4
    protected static final int NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 6
    protected static final int PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID = 2
    protected static final int PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 5

    @Autowired
    MeasurementService measurementService
    @Autowired
    AuditService auditService
    @Autowired
    ValidationService validationService

    protected void collectData(final URL url, final int siteId) {
        InputStream is = url.openStream()
        Workbook workbook = new XSSFWorkbook(is)
        processWorkbook(workbook, siteId)
        workbook.close()
    }

    abstract protected void processWorkbook(final Workbook workbook, final int siteId)

    protected static LocalDate extractCollectionDate(final Row row, int index) {
        final String value = row.getCell(index).getStringCellValue()
        return LocalDate.parse(value, DateTimeFormatter.ofPattern('MM/dd/yyyy'))
    }

    protected static BigDecimal extractValue(final Row row, int index) {
        final String value = row.getCell(index).getStringCellValue()
        return value && value.isBigDecimal() ? new BigDecimal(value) : null
    }

    protected void saveData(final int siteId,
                            final int locationId,
                            final int characteristicId,
                            final LocalDate collectionDate,
                            final BigDecimal value,
                            final BigDecimal depth = null) {

        MeasurementMaintenanceDto dto = new MeasurementMaintenanceDto(
                siteId: siteId,
                locationId: locationId,
                characteristicId: characteristicId,
                characteristicType: CharacteristicType.WATER,
                collectionDate: collectionDate, value: value,
                depth: depth)

        try {
            validationService.isValidForChange(dto)
            measurementService.save(dto)
        } catch (DataIntegrityViolationException di) {
            log.debug("Duplicate data for ${dto}", di)
        } catch (ValidationException v) {
            log.debug("Invalid data for ${dto}", v)
        } catch (Exception e) {
            log.error("Issue saving data ${dto}", e)
            auditService.audit(e)
        }
    }

}
