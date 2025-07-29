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
class WexProfileDataCollectionService extends DnrDataCollectionService {

    private static final String PROFILE_DOWNLOAD_ID = 'DLRawProfData'

    // characteristic IDs
    private static final int TEMPERATURE_PROFILE_ID = 29
    private static final int DISSOLVED_OXYGEN_PROFILE_ID = 28

    private static final int START_DATE_CELL_INDEX = 0
    private static final int START_TIME_CELL_INDEX = 1
    private static final int DEPTH_CELL_INDEX = 2
    private static final int TEMPERATURE_CELL_INDEX = 3
    private static final int DISSOX_CELL_INDEX = 4

    @Async
    @Secured('ROLE_ADMIN')
    void collectNorthPipeLakeData() {
        final URI mainPageUri = new URI(WEX_BASE_URL + NORTH_PIPE_LAKE_STATION_ID)
        final URL mainPageUrl = mainPageUri.toURL()
        final URL url = getDataDownloadUrl(mainPageUrl, PROFILE_DOWNLOAD_ID)
        collectData(url, NORTH_PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of WEx North Pipe Lake Profile Data')
    }

    @Async
    @Secured('ROLE_ADMIN')
    void collectPipeLakeData() {
        final URI mainPageUri = new URI(WEX_BASE_URL + PIPE_LAKE_STATION_ID)
        final URL mainPageUrl = mainPageUri.toURL()
        final URL url = getDataDownloadUrl(mainPageUrl, PROFILE_DOWNLOAD_ID)
        collectData(url, PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of WEx Pipe Lake Profile Data')
    }

    @Override
    protected void processWorkbook(final Workbook workbook, final int siteId) {
        final Integer deepHoleLocationId = siteId == PIPE_LAKE_SITE_ID ? PIPE_LAKE_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID
        final Sheet sheet = workbook.getSheetAt(0)
        sheet.forEach { Row row ->
            if (row.rowNum != 0) { // skipping title row
                LocalDate collectionDate = extractCollectionDate(row, START_DATE_CELL_INDEX)
                BigDecimal depth = extractValue(row, DEPTH_CELL_INDEX)
                saveData(siteId, deepHoleLocationId, TEMPERATURE_PROFILE_ID, collectionDate, extractValue(row, TEMPERATURE_CELL_INDEX), depth)
                saveData(siteId, deepHoleLocationId, DISSOLVED_OXYGEN_PROFILE_ID, collectionDate, extractValue(row, DISSOX_CELL_INDEX), depth)
            }
        }
    }

}
