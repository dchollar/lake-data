package com.lake.job

import com.lake.configuration.FtpConfig
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@CompileStatic
@Slf4j
@Component
class FtpFileProcessor {

    @Autowired
    FtpConfig ftpConfig

    void processUploadedFiles() {
        try {
            moveFiles(ftpConfig.uploadLocation, ftpConfig.processLocation)
            processFiles(ftpConfig.processLocation, ftpConfig.archiveLocation)
        } catch (Exception e) {
            log.error('Issue processing uploaded files', e)
        }
    }

    private static void moveFiles(File sourceDir, File destinationDir) {
        sourceDir.listFiles().each { File file ->
            moveFile(file, destinationDir)
        }
    }

    private static void processFiles(File sourceDir, File archiveLocation) {
        sourceDir.listFiles().each { File file ->
            processFile(file)
            archiveFile(file, archiveLocation)
        }
    }

    private static void archiveFile(File source, File archiveLocation) {
        moveFile(source, archiveLocation)
    }

    private static void moveFile(File sourceFile, File destinationDir) {
        if (sourceFile && !sourceFile.directory) {
            FileUtils.moveFileToDirectory(sourceFile, destinationDir, false)
        }
    }

    private static void processFile(File file) {
        String extension = FilenameUtils.getExtension(file.name).toLowerCase()
        if ('zip' == extension) {
            processZipFile(file)
        } else if ('csv' == extension) {
            processCsvFile(file)
        }
    }

    private static void processZipFile(File file) {
        ZipFile zipFile = new ZipFile(file)
        zipFile.entries().asIterator().each { ZipEntry entry ->
            if (!entry.directory && FilenameUtils.getExtension(entry.name).toLowerCase() == 'csv') {
                processCsvFile(zipFile.getInputStream(entry))
            }
        }
    }

    private static void processCsvFile(File file) {
        processCsvFile(FileUtils.openInputStream(file))
    }

    private static void processCsvFile(InputStream stream) {
        IOUtils.lineIterator(stream, 'UTF-8').eachWithIndex{ String line, int index ->
            if (index != 0 && line) {
                processLine(StringUtils.split(line, ','))
            }
        }
    }

    //private static final int LINE_NUMBER = 0
    private static final int DATE = 1
    private static final int BAROMETRIC_PRESSURE = 2
    private static final int WATER_PRESSURE = 3
    private static final int DIFF_PRESSURE = 4
    private static final int WATER_TEMP = 5
    private static final int WATER_LEVEL = 6
    private static final int WATER_FLOW = 7
    private static final String DATE_PATTERN = 'MM/dd/yy HH:mm:ss'
    private static final ZoneId DATA_COLLECTION_TIME_ZONE = ZoneId.of('America/Chicago')
    private static final BigDecimal SECONDS_IN_MINUTE = new BigDecimal(60)

    private static void processLine(String[] measurements) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN).withZone(DATA_COLLECTION_TIME_ZONE)
        Instant collectionDateTime = ZonedDateTime.parse(measurements[DATE], formatter).toInstant()
        BigDecimal barometricPressure = new BigDecimal(measurements[BAROMETRIC_PRESSURE])
        BigDecimal waterPressure = new BigDecimal(measurements[WATER_PRESSURE])
        BigDecimal diffPressure = new BigDecimal(measurements[DIFF_PRESSURE])
        BigDecimal waterTemp = new BigDecimal(measurements[WATER_TEMP])
        BigDecimal waterLevel = new BigDecimal(measurements[WATER_LEVEL])
        BigDecimal waterFlow = ((new BigDecimal(measurements[WATER_FLOW])) * SECONDS_IN_MINUTE).round(4)  // convert from cfs to cfm

        log.info("data values are: $collectionDateTime, $barometricPressure, $waterPressure, $diffPressure, $waterTemp, $waterLevel, $waterFlow")
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