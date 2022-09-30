package com.lake.job

import com.lake.configuration.FtpConfig
import com.lake.service.AuditService
import com.lake.service.MeasurementFileService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@CompileStatic
@Slf4j
@Component
class FtpFileProcessor {

    private static final String REPORTER_USERNAME = 'ftp'

    @Autowired
    FtpConfig ftpConfig
    @Autowired
    AuditService auditService
    @Autowired
    MeasurementFileService measurementFileService

    @Scheduled(cron = "0 0 1 * * *")
    //@Scheduled(cron = "0 */5 * * * *") // for testing
    void processFilesJob() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(REPORTER_USERNAME, 'ftp_password')
        SecurityContextHolder.getContext().setAuthentication(token)
        processUploadedFiles()
    }

    @Secured('ROLE_ADMIN')
    void processUploadedFiles() {
        try {
            moveFiles(ftpConfig.uploadLocation, ftpConfig.processLocation)
            processFiles(ftpConfig.processLocation, ftpConfig.archiveLocation, ftpConfig.errorLocation)
        } catch (Exception e) {
            log.error('Issue processing uploaded files', e)
            auditService.audit(e)
        }
    }

    private static void moveFiles(File sourceDir, File destinationDir) {
        sourceDir?.listFiles()?.each { File file ->
            moveFile(file, destinationDir)
        }
    }

    private void processFiles(File sourceDir, File archiveLocation, File errorLocation) {
        sourceDir?.listFiles()?.each { File file ->
            try {
                processFile(file)
                moveFile(file, archiveLocation)
            } catch (Exception e) {
                log.error('Issue processing the file', e)
                auditService.audit(e)
                moveFile(file, errorLocation)
            }
        }
    }

    private static void moveFile(File sourceFile, File destinationDir) {
        if (sourceFile && !sourceFile.isDirectory() && destinationDir && destinationDir.isDirectory()) {
            FileUtils.moveFileToDirectory(sourceFile, destinationDir, false)
        }
    }

    private void processFile(File file) {
        if (file) {
            auditService.audit('JOB', file.name, this.class.simpleName)
            String extension = FilenameUtils.getExtension(file.name).toLowerCase()
            if ('zip' == extension) {
                measurementFileService.processZipFile(file)
            } else if ('csv' == extension) {
                measurementFileService.processCsvFile(file)
            }
        }
    }

}