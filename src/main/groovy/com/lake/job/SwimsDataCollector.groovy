package com.lake.job

import com.lake.dto.SavedMeasurementDto
import com.lake.entity.Reporter
import com.lake.entity.UnitType
import com.lake.service.AuditService
import com.lake.service.MeasurementService
import com.lake.service.ReporterService
import com.lake.service.ValidationService
import groovy.util.logging.Slf4j
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NodeChild
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

import javax.xml.bind.ValidationException
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter

@Slf4j
@Component
class SwimsDataCollector {

    public static final String FIRST_YEAR = '1950'
    private static final String REPORTER_USERNAME = 'swims'
    private static final String CURRENT_YEAR = 'CURRENT_YEAR'
    private static final String START_YEAR = 'START_YEAR'
    private static final String PIPE_LAKE_NAME = 'Pipe Lake'

    // site and location ids
    private static final int PIPE_LAKE_SITE_ID = 1
    private static final int NORTH_PIPE_LAKE_SITE_ID = 2
    private static final int NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 6
    private static final int PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 5

    // UNIT IDs
    private static final int SECCHI_ID = 9
    private static final int CHLOROPHYLL_ID = 10
    private static final int TOTAL_PHOSPHORUS_ID = 1
    private static final int TSI_SD_ID = 11
    private static final int TSI_TP_ID = 12
    private static final int TSI_CHL_ID = 13
    private static final int TEMPERATURE_PROFILE_ID = 29
    private static final int DISSOLVED_OXYGEN_PROFILE_ID = 28

    private static final BigDecimal FOOT = new BigDecimal(3.28084)

    private static final List<String> urls = ['http://dnrx.wisconsin.gov/swims/public/reporting.do?type=58&action=post&stationNo=493105&year1=START_YEAR&year2=CURRENT_YEAR&format=xml',
                                              'http://dnrx.wisconsin.gov/swims/public/reporting.do?type=58&action=post&stationNo=493097&year1=START_YEAR&year2=CURRENT_YEAR&format=xml']

    @Autowired
    MeasurementService measurementService
    @Autowired
    ReporterService reporterService
    @Autowired
    AuditService auditService
    @Autowired
    ValidationService validationService

    @Scheduled(cron = "0 0 0 1 * *")
    void fetchData() {
        String year = Year.now().getValue().toString()
        fetchDataInternal(year, year)
    }

    void fetchData(String year1) {
        fetchDataInternal(year1, Year.now().getValue().toString())
    }

    private List<String> fetchDataInternal(String year1, String year2) {
        auditService.audit('JOB', "fetchData ${year1} ${year2}", this.class.simpleName)
        urls.each {
            String urlString = it.replace(CURRENT_YEAR, year2).replace(START_YEAR, year1)
            log.info(urlString)
            getSwimData(urlString)
        }
    }

    private void getSwimData(String urlString) {
        URL url = findRealUrl(urlString.toURL())
        String xml = IOUtils.toString(url, StandardCharsets.UTF_8)
        parseXml(xml)
    }

    private void parseXml(String xml) {
        Reporter reporter = reporterService.getReporter(REPORTER_USERNAME)

        GPathResult clmnAnnualReport = new XmlSlurper().parseText(xml)
        String lakeName = StringUtils.stripToNull(clmnAnnualReport.srow.official_name.toString())
        Integer siteId = lakeName == PIPE_LAKE_NAME ? PIPE_LAKE_SITE_ID : NORTH_PIPE_LAKE_SITE_ID
        Integer locationId = lakeName == PIPE_LAKE_NAME ? PIPE_LAKE_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID

        clmnAnnualReport.srow.secchi_rows.secchi_row.each { NodeChild row ->
            processSecchiRow(reporter, siteId, locationId, row)
        }
        clmnAnnualReport.srow.profile_rows.profile_row.each { NodeChild row ->
            processProfileRow(reporter, siteId, locationId, row)
        }
    }

    private void processSecchiRow(Reporter reporter, Integer siteId, Integer locationId, NodeChild row) {
        String startDate = StringUtils.stripToNull(row.start_date.toString())
        saveDto(reporter, siteId, locationId, SECCHI_ID, startDate, StringUtils.stripToNull(row.secchi.toString()))
        saveDto(reporter, siteId, locationId, CHLOROPHYLL_ID, startDate, StringUtils.stripToNull(row.chlorophyll.toString()))
        saveDto(reporter, siteId, locationId, TOTAL_PHOSPHORUS_ID, startDate, StringUtils.stripToNull(row.total_phosphorus.toString()))
        saveDto(reporter, siteId, locationId, TSI_SD_ID, startDate, StringUtils.stripToNull(row.TSI_SD.toString()))
        saveDto(reporter, siteId, locationId, TSI_TP_ID, startDate, StringUtils.stripToNull(row.TSI_TP.toString()))
        saveDto(reporter, siteId, locationId, TSI_CHL_ID, startDate, StringUtils.stripToNull(row.TSI_CHL.toString()))
    }

    private void processProfileRow(Reporter reporter, Integer siteId, Integer locationId, NodeChild row) {
        String startDate = StringUtils.stripToNull(row.start_date2.toString())
        String depth = extractDepth(row)
        saveDto(reporter, siteId, locationId, TEMPERATURE_PROFILE_ID, startDate, extractTemperature(row), depth)
        saveDto(reporter, siteId, locationId, DISSOLVED_OXYGEN_PROFILE_ID, startDate, extractDissolvedOxygen(row), depth)
    }

    private static String extractDissolvedOxygen(final NodeChild row) {
        return StringUtils.stripToNull(row.dissolved_oxygen.toString())
    }

    private static String extractDepth(final NodeChild row) {
        String depth = StringUtils.stripToNull(row.depth.toString())
        String depthUnits = StringUtils.stripToNull(row.childNodes()[1].attributes().units.toString())
        if (depth && depthUnits && depthUnits == 'METERS') {
            BigDecimal meters = new BigDecimal(depth)
            depth = FOOT.multiply(meters).round(0).toString()
        }
        depth
    }

    private static String extractTemperature(final NodeChild row) {
        String temp = StringUtils.stripToNull(row.temperature.toString())
        String temperatureUnits = StringUtils.stripToNull(row.childNodes()[2].attributes().units.toString())
        if (temp && temperatureUnits && temperatureUnits.contains('C')) {
            if (temp.contains('F')) {
                // some bad data appears to be encoded as celsius but is not
                temp = temp.replace('F', '').trim()
            } else {
                // convert to fahrenheit
                BigDecimal c = new BigDecimal(temp)
                temp = ((c * 9.0 / 5.0) + 32.0).round(1).toString()
            }
        }
        temp
    }

    private void saveDto(Reporter reporter, Integer siteId, Integer locationId, Integer unitId, String collectionDate, String value, String depth = null) {
        SavedMeasurementDto dto = new SavedMeasurementDto(siteId: siteId, locationId: locationId, unitId: unitId, unitType: UnitType.WATER)
        dto.collectionDate = LocalDate.parse(collectionDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        dto.value = value && value.isBigDecimal() ? new BigDecimal(value) : null
        dto.depth = depth && depth.isBigDecimal() ? new BigDecimal(depth) : null

        try {
            validationService.isValidForChange(dto)
            measurementService.save(dto, reporter)
        } catch (DataIntegrityViolationException di) {
            log.debug("Duplicate data for ${dto}", di)
        } catch (ValidationException v) {
            log.debug("Invalid data for ${dto}", v)
        } catch (Exception e) {
            log.error("Issue saving data ${dto}", e)
        }
    }

    /**
     * Need to follow all the redirects in order to get to the real site.
     * Method is recursive.
     * @param url
     * @return the url after no more redirects
     */
    private URL findRealUrl(URL url) {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection()
        conn.followRedirects = false
        conn.requestMethod = 'HEAD'
        if (conn.responseCode in [301, 302]) {
            if (conn.headerFields.'Location') {
                return findRealUrl(conn.headerFields.Location.first().toURL())
            } else {
                throw new RuntimeException('Failed to follow redirect')
            }
        }
        return url
    }

}
