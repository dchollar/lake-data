package com.lake.job

import com.lake.dto.MeasurementMaintenanceDto
import com.lake.entity.CharacteristicType
import com.lake.service.AuditService
import com.lake.service.MeasurementService
import com.lake.service.ReporterService
import com.lake.service.ValidationService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.Node
import groovy.xml.slurpersupport.NodeChild
import jakarta.validation.ValidationException
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.format.DateTimeFormatter

@CompileStatic
@Slf4j
@Component
class SwimsDataCollector {

    public static final String FIRST_YEAR = '1950'
    private static final String REPORTER_USERNAME = 'swims'
    private static final String CURRENT_YEAR = 'CURRENT_YEAR'
    private static final String START_YEAR = 'START_YEAR'
    private static final String PIPE_LAKE_NAME = 'Pipe Lake'

    // site ids
    private static final int PIPE_LAKE_SITE_ID = 1
    private static final int NORTH_PIPE_LAKE_SITE_ID = 2

    // location ids
    private static final int NORTH_PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID = 4
    private static final int NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 6
    private static final int PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID = 2
    private static final int PIPE_LAKE_DEEP_HOLE_LOCATION_ID = 5

    // characteristic IDs
    private static final int SECCHI_ID = 9
    private static final int CHLOROPHYLL_ID = 10
    private static final int TOTAL_PHOSPHORUS_ID = 1
    private static final int TSI_SD_ID = 11
    private static final int TSI_TP_ID = 12
    private static final int TSI_CHL_ID = 13
    private static final int TEMPERATURE_PROFILE_ID = 29
    private static final int DISSOLVED_OXYGEN_PROFILE_ID = 28

    private static final BigDecimal FOOT = 3.28084 //number of feet in a meter

    private static final List<String> urls = ['https://dnrx.wisconsin.gov/swims/public/reporting.do?type=58&action=post&stationNo=493105&year1=START_YEAR&year2=CURRENT_YEAR&format=xml',
                                              'https://dnrx.wisconsin.gov/swims/public/reporting.do?type=58&action=post&stationNo=493097&year1=START_YEAR&year2=CURRENT_YEAR&format=xml']

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
        try {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(REPORTER_USERNAME, 'swims_password')
            SecurityContextHolder.getContext().setAuthentication(token)
            final String year = (Month.from(Instant.now()) == Month.JANUARY) ? lastYear() : currentYear()
            fetchDataInternal(year, year)
        } catch (Exception e) {
            log.error('Issue processing SWIMS data', e)
            auditService.audit(e)
        }
    }

    // This is called from the controller
    @Secured('ROLE_ADMIN')
    void fetchData(String year1) {
        fetchDataInternal(year1, currentYear())
    }

    static String currentYear() {
        return Year.now().getValue().toString()
    }

    static String lastYear() {
        int lastYear = Year.now().getValue() - 1
        return lastYear.toString()
    }

    private void fetchDataInternal(String year1, String year2) {
        auditService.audit('JOB', "fetchData ${year1} ${year2}", this.class.simpleName)
        urls.each {
            String urlString = it.replace(CURRENT_YEAR, year2).replace(START_YEAR, year1)
            log.debug(urlString)
            getSwimData(urlString)
        }
    }

    private void getSwimData(String urlString) {
        URL url = findRealUrl(urlString.toURL())
        String xml = IOUtils.toString(url, StandardCharsets.UTF_8)
        parseXml(xml)
    }

    /**
     * The xml file has the site name in it. Based on the site name and the characteristic being
     * parsed, we need to determine the location. Might need to set up a mapping table with these
     * elements instead of hard coding them here. Only parsing North Pipe Lake and Pipe Lake data.
     *
     * @param xml
     */
    private void parseXml(String xml) {
        GPathResult clmnAnnualReport = new XmlSlurper().parseText(xml)

        GPathResult srow = clmnAnnualReport.getProperty('srow') as GPathResult
        if (srow) {
            String lakeName = StringUtils.stripToNull(srow?.getProperty('official_name')?.toString())
            if (lakeName) {
                Integer siteId = lakeName == PIPE_LAKE_NAME ? PIPE_LAKE_SITE_ID : NORTH_PIPE_LAKE_SITE_ID
                Integer topDeepHoleLocationId = lakeName == PIPE_LAKE_NAME ? PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_TOP_DEEP_HOLE_LOCATION_ID
                Integer deepHoleLocationId = lakeName == PIPE_LAKE_NAME ? PIPE_LAKE_DEEP_HOLE_LOCATION_ID : NORTH_PIPE_LAKE_DEEP_HOLE_LOCATION_ID

                processSecchiRows(srow, siteId, topDeepHoleLocationId, deepHoleLocationId)
                processProfileRows(srow, siteId, deepHoleLocationId)
            }
        }
    }

    private void processProfileRows(GPathResult srow, int siteId, int deepHoleLocationId) {
        GPathResult profileRows = srow?.getProperty('profile_rows') as GPathResult
        GPathResult profileRow = profileRows?.getProperty('profile_row') as GPathResult
        profileRow?.each { Object row ->
            if (row) {
                processProfileRow(siteId, deepHoleLocationId, row as NodeChild)
            }
        }
    }

    private void processSecchiRows(GPathResult srow, int siteId, int topDeepHoleLocationId, int deepHoleLocationId) {
        GPathResult secchiRows = srow?.getProperty('secchi_rows') as GPathResult
        GPathResult secchiRow = secchiRows?.getProperty('secchi_row') as GPathResult
        secchiRow?.each { Object row ->
            if (row) {
                processSecchiRow(siteId, topDeepHoleLocationId, deepHoleLocationId, row as NodeChild)
            }
        }
    }

    private void processSecchiRow(Integer siteId, Integer topDeepHoleLocationId, Integer deepHoleLocationId, NodeChild row) {
        String startDate = StringUtils.stripToNull(row.getProperty('start_date')?.toString())
        saveDto(siteId, deepHoleLocationId, SECCHI_ID, startDate, StringUtils.stripToNull(row.getProperty('secchi')?.toString()))
        saveDto(siteId, topDeepHoleLocationId, CHLOROPHYLL_ID, startDate, StringUtils.stripToNull(row.getProperty('chlorophyll')?.toString()))
        saveDto(siteId, topDeepHoleLocationId, TOTAL_PHOSPHORUS_ID, startDate, StringUtils.stripToNull(row.getProperty('total_phosphorus')?.toString()))
        saveDto(siteId, topDeepHoleLocationId, TSI_SD_ID, startDate, StringUtils.stripToNull(row.getProperty('TSI_SD')?.toString()))
        saveDto(siteId, topDeepHoleLocationId, TSI_TP_ID, startDate, StringUtils.stripToNull(row.getProperty('TSI_TP')?.toString()))
        saveDto(siteId, topDeepHoleLocationId, TSI_CHL_ID, startDate, StringUtils.stripToNull(row.getProperty('TSI_CHL')?.toString()))
    }

    private void processProfileRow(Integer siteId, Integer deepHoleLocationId, NodeChild row) {
        String startDate = StringUtils.stripToNull(row.getProperty('start_date2')?.toString())
        String depth = extractDepth(row)
        saveDto(siteId, deepHoleLocationId, TEMPERATURE_PROFILE_ID, startDate, extractTemperature(row), depth)
        saveDto(siteId, deepHoleLocationId, DISSOLVED_OXYGEN_PROFILE_ID, startDate, extractDissolvedOxygen(row), depth)
    }

    private static String extractDissolvedOxygen(final NodeChild row) {
        return StringUtils.stripToNull(row.getProperty('dissolved_oxygen')?.toString())
    }

    private static String extractDepth(final NodeChild row) {
        final String nodeName = 'depth'
        final String depth = StringUtils.stripToNull(row.getProperty(nodeName)?.toString())
        final Node depthNode = row.childNodes().find { Node node -> node.name() == nodeName } as Node
        final String depthUnits = StringUtils.stripToNull(depthNode?.attributes()?.get('units')?.toString())
        if (depth && depthUnits && depthUnits == 'METERS') {
            BigDecimal meters = new BigDecimal(depth)
            return (FOOT * meters).round(0).toString()
        } else {
            return depth
        }
    }

    private static String extractTemperature(final NodeChild row) {
        final String nodeName = 'temperature'
        final String temp = StringUtils.stripToNull(row.getProperty(nodeName)?.toString())
        final Node tempNode = row.childNodes().find { Node node -> node.name() == nodeName } as Node
        final String temperatureUnits = StringUtils.stripToNull(tempNode?.attributes()?.get('units')?.toString())
        if (temp && temperatureUnits && temperatureUnits.contains('C')) {
            if (temp.contains('F')) {
                // some bad data appears to be encoded as celsius but is not
                return temp.replace('F', '').trim()
            } else {
                // convert to fahrenheit
                BigDecimal c = new BigDecimal(temp)
                return ((c * 9.0 / 5.0) + 32.0).round(1).toString()
            }
        }
        return temp
    }

    private void saveDto(Integer siteId,
                         Integer locationId,
                         Integer characteristicId,
                         String collectionDate,
                         String value,
                         String depth = null) {
        MeasurementMaintenanceDto dto = new MeasurementMaintenanceDto(siteId: siteId, locationId: locationId, characteristicId: characteristicId, characteristicType: CharacteristicType.WATER)
        dto.collectionDate = LocalDate.parse(collectionDate, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        dto.value = value && value.isBigDecimal() ? new BigDecimal(value) : null
        dto.depth = depth && depth.isBigDecimal() ? new BigDecimal(depth) : null

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
