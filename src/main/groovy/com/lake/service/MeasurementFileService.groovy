package com.lake.service

import com.lake.entity.Location
import com.lake.job.FileType
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
@Slf4j
@Service
class MeasurementFileService {

    @Autowired
    LocationService locationService
    @Autowired
    SiteService siteService
    @Autowired
    MeasurementDetailService measurementDetailService

    private static final Integer STREAM_SITE_ID = 4
    private static final Integer HEADERS_IN_AUTO_FILE = 20

    private static final int BAROMETRIC_PRESSURE_CHARACTERISTIC_ID = 40
    private static final int WATER_PRESSURE_CHARACTERISTIC_ID = 41
    private static final int WATER_TEMP_CHARACTERISTIC_ID = 37
    private static final int WATER_LEVEL_CHARACTERISTIC_ID = 42
    private static final int WATER_FLOW_CHARACTERISTIC_ID = 26

    // column positions for automatic data file
    //private static final int LINE_NUMBER = 0
    private static final int DATE = 1
    private static final int BAROMETRIC_PRESSURE = 2 // Characteristic id = 40
    private static final int WATER_PRESSURE = 3 // Characteristic id = 41
    //private static final int DIFF_PRESSURE = 4
    private static final int WATER_TEMP = 5 // Characteristic id = 37
    private static final int WATER_LEVEL = 6 // Characteristic id = 42
    private static final int WATER_FLOW = 7 // Characteristic id = 26
    private static final String DATE_PATTERN = 'MM/dd/yy HH:mm:ss'

    // column positions for manual data file
    private static final int MANUAL_LINE_NUMBER = 0
    private static final int MANUAL_DATE = 1
    private static final int MANUAL_WATER_PRESSURE = 2 // Characteristic id = 41
    private static final int MANUAL_WATER_TEMP = 3 // Characteristic id = 37
    private static final int MANUAL_BAROMETRIC_PRESSURE = 4 // Characteristic id = 40
    private static final int MANUAL_WATER_LEVEL = 5 // Characteristic id = 42
    private static final String MANUAL_DATE_PATTERN = 'MM/dd/yy hh:mm:ss a'

    private static final ZoneId DATA_COLLECTION_TIME_ZONE = ZoneId.of('America/Chicago')
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal(60)

    @Secured('ROLE_ADMIN')
    @Transactional
    void saveDataFile(final MultipartFile dataFile, final Location location) {
        String fileName = dataFile.originalFilename
        String extension = FilenameUtils.getExtension(fileName).toLowerCase()
        if ('zip' == extension) {
            File tmpFile = File.createTempFile('lake-data-', '-zip')
            dataFile.transferTo(tmpFile)
            processZipFile(tmpFile, location)
            tmpFile.delete()
        } else if ('csv' == extension) {
            processCsvStream(dataFile.inputStream, location, dataFile.originalFilename)
        }
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void processZipFile(final File file, final Location location = null) {
        ZipFile zipFile = new ZipFile(file)
        zipFile.entries().asIterator().each { ZipEntry entry ->
            if (!entry.directory && FilenameUtils.getExtension(entry.name).toLowerCase() == 'csv') {
                processCsvStream(zipFile.getInputStream(entry), location, zipFile.name)
            }
        }
        zipFile.close()
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void processCsvFile(final File file) {
        processCsvStream(FileUtils.openInputStream(file), file.name)
    }

    private void processCsvStream(final InputStream stream, final Location location = null, final String filename) {
        FileType fileType = FileType.UNKNOWN
        Location loc = location
        IOUtils.lineIterator(stream, 'UTF-8').eachWithIndex { String line, int index ->
            if (line) {
                if (index == 0) {
                    final String[] headers = StringUtils.split(line, '",')
                    fileType = findFileType(headers)
                    if (location == null) {
                        loc = findLocation(headers)
                    }
                } else {
                    processLine(fileType, StringUtils.split(line, ','), loc, filename)
                }
            }
        }
        IOUtils.close(stream)
        measurementDetailService.summarize()
    }

    private static FileType findFileType(final String[] headers) {
        if (headers.size() == HEADERS_IN_AUTO_FILE) {
            return FileType.AUTO
        } else if (isManualFileHeader(headers)) {
            return FileType.MANUALLY
        } else {
            throw new RuntimeException('File type unknown')
        }
    }

    private static boolean isManualFileHeader(final String[] headers) {
        String header = headers.find { it.toUpperCase().contains('PLOT TITLE') }
        return header != null
    }

    private Location findLocation(final String[] headers) {
        if (headers.size() == HEADERS_IN_AUTO_FILE) {
            String locationDescription = headers[4].strip().toUpperCase()
            // TODO currently assuming the site is always a stream
            return locationService.getByDescription(siteService.getOne(STREAM_SITE_ID), locationDescription)
        }
        return null
    }

    private void processLine(final FileType fileType, final String[] measurements, final Location location, final String filename) {
        if (location && fileType && measurements.length > 1) {
            if (fileType == FileType.AUTO) {
                processAutoLine(measurements, location, filename)
            } else if (fileType == FileType.MANUALLY) {
                processManualLine(measurements, location, filename)
            }
        }
    }

    private void processAutoLine(final String[] measurements, final Location location, final String filename) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(DATA_COLLECTION_TIME_ZONE)
        Instant collectionDateTime = ZonedDateTime.parse(measurements[DATE], formatter).toInstant()

        BigDecimal barometricPressure = new BigDecimal(measurements[BAROMETRIC_PRESSURE])
        persist(location, BAROMETRIC_PRESSURE_CHARACTERISTIC_ID, barometricPressure, collectionDateTime, filename)

        BigDecimal waterPressure = new BigDecimal(measurements[WATER_PRESSURE])
        persist(location, WATER_PRESSURE_CHARACTERISTIC_ID, waterPressure, collectionDateTime, filename)

        //BigDecimal diffPressure = new BigDecimal(measurements[DIFF_PRESSURE])

        BigDecimal waterTemp = new BigDecimal(measurements[WATER_TEMP])
        persist(location, WATER_TEMP_CHARACTERISTIC_ID, waterTemp, collectionDateTime, filename)

        BigDecimal waterLevel = new BigDecimal(measurements[WATER_LEVEL])
        persist(location, WATER_LEVEL_CHARACTERISTIC_ID, waterLevel, collectionDateTime, filename)

        BigDecimal waterFlow = ((new BigDecimal(measurements[WATER_FLOW])) * SECONDS_IN_MINUTE).round(4)  // convert from cfs to cfm
        persist(location, WATER_FLOW_CHARACTERISTIC_ID, waterFlow, collectionDateTime, filename)
    }

    private void processManualLine(final String[] measurements, final Location location, final String filename) {
        // TODO need to calculate water flow rate
        int lineLength = measurements.size()
        if (StringUtils.isNumeric(measurements[MANUAL_LINE_NUMBER]) && lineLength >= 6) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(MANUAL_DATE_PATTERN).withZone(DATA_COLLECTION_TIME_ZONE)
            Instant collectionDateTime = ZonedDateTime.parse(measurements[MANUAL_DATE], formatter).toInstant()

            BigDecimal waterPressure = new BigDecimal(measurements[MANUAL_WATER_PRESSURE])
            persist(location, WATER_PRESSURE_CHARACTERISTIC_ID, waterPressure, collectionDateTime, filename)

            BigDecimal waterTemp = new BigDecimal(measurements[MANUAL_WATER_TEMP])
            persist(location, WATER_TEMP_CHARACTERISTIC_ID, waterTemp, collectionDateTime, filename)

            BigDecimal barometricPressure = new BigDecimal(measurements[MANUAL_BAROMETRIC_PRESSURE])
            persist(location, BAROMETRIC_PRESSURE_CHARACTERISTIC_ID, barometricPressure, collectionDateTime, filename)

            BigDecimal waterLevel = new BigDecimal(measurements[MANUAL_WATER_LEVEL])
            persist(location, WATER_LEVEL_CHARACTERISTIC_ID, waterLevel, collectionDateTime, filename)
        }
    }

    private void persist(final Location location, final Integer characteristicId, final BigDecimal value, final Instant collectionDateTime, final String filename) {
        try {
            measurementDetailService.save(location, characteristicId, collectionDateTime, value, filename)
        } catch (Exception e) {
            log.debug(ExceptionUtils.getRootCauseMessage(e))
        }
    }
}


// Example data
//
// Line#,
// Date,
// "Barometric Pressure (M-BP 21334508:21334508-1) psi NPI-W1",
// "Water Pressure (M-WP04 21334508:21382357-1) psi NPI-W1",
// "Diff Pressure (M-DP04 21334508:21382357-2) psi NPI-W1",
// "Water Temperature (M-WT 21334508:21382357-3) *F NPI-W1",
// "Water Level (M-WL04 21334508:21382357-4) feet NPI-W1",
// "Water Flow (M-WF-VNW 21334508:21382357-5) cfs NPI-W1"

//1,04/13/22 10:45:00,13.8540,14.4765,0.6225,40.5,0.8026,0.8314
//2,04/13/22 10:55:00,13.8504,14.4713,0.6209,40.5,0.7989,0.8220
//3,04/13/22 11:05:00,13.8536,14.4733,0.6197,40.5,0.7960,0.8147
//4,04/13/22 11:15:00,13.8508,14.4696,0.6188,40.7,0.7939,0.8093
//5,04/13/22 11:25:00,13.8528,14.4689,0.6161,40.7,0.7877,0.7937