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
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
    //@Scheduled(cron = "0 */5 * * * *")
    void processFilesJob() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(REPORTER_USERNAME, 'ftp_password')
        SecurityContextHolder.getContext().setAuthentication(token)
        processUploadedFiles()
    }

    @Secured('ROLE_ADMIN')
    void processUploadedFiles() {
        try {
            moveFiles(ftpConfig.uploadLocation, ftpConfig.processLocation)
            processFiles(ftpConfig.processLocation, ftpConfig.archiveLocation)
        } catch (Exception e) {
            log.error('Issue processing uploaded files', e)
            auditService.audit(e)
        }
    }

    private static void moveFiles(File sourceDir, File destinationDir) {
        sourceDir.listFiles().each { File file ->
            moveFile(file, destinationDir)
        }
    }

    private void processFiles(File sourceDir, File archiveLocation) {
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

    void processFile(File file) {
        auditService.audit('JOB', file.name, this.class.simpleName)
        String extension = FilenameUtils.getExtension(file.name).toLowerCase()
        if ('zip' == extension) {
            measurementFileService.processZipFile(file)
        } else if ('csv' == extension) {
            measurementFileService.processCsvFile(file)
        }
    }

}