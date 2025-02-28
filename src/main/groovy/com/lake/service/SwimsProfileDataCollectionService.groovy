package com.lake.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.springframework.scheduling.annotation.Async
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

import java.time.LocalDate

@CompileStatic
@Slf4j
@Service
class SwimsProfileDataCollectionService extends DnrDataCollectionService {

    private static final String BASE_URL = 'https://apps.dnr.wi.gov/swims/LakesReport/DownloadTemperatureReport?stationId='

    private static final BigDecimal FOOT = 3.28084 //number of feet in a meter

    // characteristic IDs
    private static final int TEMPERATURE_PROFILE_ID = 29
    private static final int DISSOLVED_OXYGEN_PROFILE_ID = 28

    private static final int START_DATE_CELL_INDEX = 0
    private static final int START_MONTH_CELL_INDEX = 1
    private static final int START_DAY_CELL_INDEX = 2
    private static final int START_YEAR_CELL_INDEX = 3
    private static final int RESULT_DEPTH_AMT_CELL_INDEX = 4
    private static final int RESULT_DEPTH_UNIT_CELL_INDEX = 5
    private static final int TEMPERATURE_CELL_INDEX = 6
    private static final int DISSOX_CELL_INDEX = 7
    private static final int TEMPERATURE_UNITS_CELL_INDEX = 8
    private static final int DISSOX_UNITS_CELL_INDEX = 9

    @Async
    @Secured('ROLE_ADMIN')
    void collectNorthPipeLakeData() {
        final URL url = new URL(BASE_URL + NORTH_PIPE_LAKE_STATION_ID)
        collectData(url, NORTH_PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of SWIMS North Pipe Lake Profile Data')
    }

    @Async
    @Secured('ROLE_ADMIN')
    void collectPipeLakeData() {
        final URL url = new URL(BASE_URL + PIPE_LAKE_STATION_ID)
        collectData(url, PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of SWIMS Pipe Lake Profile Data')
    }

    @Override
    protected void processWorkbook(final Workbook workbook, final int siteId) {
        final Integer deepHoleLocationId = siteId == PIPE_LAKE_SITE_ID ? PIPE_LAKE_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID
        final Sheet sheet = workbook.getSheetAt(0)
        sheet.forEach { Row row ->
            if (row.rowNum != 0) { // skipping title row
                LocalDate collectionDate = extractCollectionDate(row, START_DATE_CELL_INDEX)
                BigDecimal depth = extractDepth(row)
                saveData(siteId, deepHoleLocationId, TEMPERATURE_PROFILE_ID, collectionDate, extractTemperature(row), depth)
                saveData(siteId, deepHoleLocationId, DISSOLVED_OXYGEN_PROFILE_ID, collectionDate, extractValue(row, DISSOX_CELL_INDEX), depth)
            }
        }
    }

    private static BigDecimal extractDepth(final Row row) {
        final String value = row.getCell(RESULT_DEPTH_AMT_CELL_INDEX).getStringCellValue()
        final String depthUnits = row.getCell(RESULT_DEPTH_UNIT_CELL_INDEX).getStringCellValue()
        if (value && value.isBigDecimal() && depthUnits && depthUnits == 'METERS') {
            BigDecimal meters = new BigDecimal(value)
            return (FOOT * meters).round(0)
        } else {
            return value && value.isBigDecimal() ? new BigDecimal(value) : null
        }
    }

    private static BigDecimal extractTemperature(final Row row) {
        final String temp = row.getCell(TEMPERATURE_CELL_INDEX).getStringCellValue()
        final String temperatureUnits = row.getCell(TEMPERATURE_UNITS_CELL_INDEX).getStringCellValue()
        final value
        if (temp && temperatureUnits && temperatureUnits.contains('C')) {
            if (temp.contains('F')) {
                // some bad data appears to be encoded as celsius but is not
                value = temp.replace('F', '').trim()
            } else {
                // convert to fahrenheit
                BigDecimal c = new BigDecimal(temp)
                value = ((c * 9.0 / 5.0) + 32.0).round(1).toString()
            }
        } else {
            value = temp
        }
        return value && value.isBigDecimal() ? new BigDecimal(value) : null
    }

}
