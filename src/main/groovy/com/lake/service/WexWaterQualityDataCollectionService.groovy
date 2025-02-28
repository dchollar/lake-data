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
class WexWaterQualityDataCollectionService extends DnrDataCollectionService {

    private static final String TREND_DATA_DOWNLOAD_ID = 'DLRawTrendData'

    // characteristic IDs
    private static final int SECCHI_ID = 9
    private static final int CHLOROPHYLL_ID = 10
    private static final int TOTAL_PHOSPHORUS_ID = 1
    private static final int TSI_SD_ID = 11
    private static final int TSI_TP_ID = 12
    private static final int TSI_CHL_ID = 13

    private static final int SECCHI_CELL_INDEX = 0
    private static final int SECCHI_HIT_BOTTOM_CELL_INDEX = 1
    private static final int TSI_SD_CELL_INDEX = 2
    private static final int TP_CELL_INDEX = 3
    private static final int TSI_TP_CELL_INDEX = 4
    private static final int CHL_CELL_INDEX = 5
    private static final int TSI_CHL_CELL_INDEX = 6
    private static final int COLOR_CELL_INDEX = 7
    private static final int PERCEPTION_CELL_INDEX = 8
    private static final int START_DATE_CELL_INDEX = 9
    private static final int START_YEAR_CELL_INDEX = 10


    @Async
    @Secured('ROLE_ADMIN')
    void collectNorthPipeLakeData() {
        final URL mainPageUrl = new URL(WEX_BASE_URL + NORTH_PIPE_LAKE_STATION_ID)
        final URL url = getDataDownloadUrl(mainPageUrl, TREND_DATA_DOWNLOAD_ID)
        collectData(url, NORTH_PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of WEx North Pipe Lake Water Quality Data')
    }

    @Async
    @Secured('ROLE_ADMIN')
    void collectPipeLakeData() {
        final URL mainPageUrl = new URL(WEX_BASE_URL + PIPE_LAKE_STATION_ID)
        final URL url = getDataDownloadUrl(mainPageUrl, TREND_DATA_DOWNLOAD_ID)
        collectData(url, PIPE_LAKE_SITE_ID)
        log.info('Completed the collection of WEx Pipe Lake Water Quality Data')
    }

    @Override
    protected void processWorkbook(final Workbook workbook, final int siteId) {
        final int deepHoleLocationId = siteId == PIPE_LAKE_SITE_ID ? PIPE_LAKE_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID
        final int topDeepHoleLocationId = siteId == PIPE_LAKE_SITE_ID ? PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID

        final Sheet sheet = workbook.getSheetAt(0)
        sheet.forEach { Row row ->
            if (row.rowNum != 0) { // skipping title row
                LocalDate collectionDate = extractCollectionDate(row, START_DATE_CELL_INDEX)
                saveData(siteId, deepHoleLocationId, SECCHI_ID, collectionDate, extractValue(row, SECCHI_CELL_INDEX))
                saveData(siteId, topDeepHoleLocationId, CHLOROPHYLL_ID, collectionDate, extractValue(row, CHL_CELL_INDEX))
                saveData(siteId, topDeepHoleLocationId, TOTAL_PHOSPHORUS_ID, collectionDate, extractValue(row, TP_CELL_INDEX))
                saveData(siteId, topDeepHoleLocationId, TSI_SD_ID, collectionDate, extractValue(row, TSI_SD_CELL_INDEX))
                saveData(siteId, topDeepHoleLocationId, TSI_TP_ID, collectionDate, extractValue(row, TSI_TP_CELL_INDEX))
                saveData(siteId, topDeepHoleLocationId, TSI_CHL_ID, collectionDate, extractValue(row, TSI_CHL_CELL_INDEX))
            }
        }
    }

}
