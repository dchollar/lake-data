package com.lake.controller


import com.lake.job.FtpFileProcessor
import com.lake.service.AuditService
import com.lake.service.ProfileDataCollectionService
import com.lake.service.WaterQualityDataCollectionService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@CompileStatic
@Slf4j
@Controller
class JobController {

    private static final String REDIRECT_HOME = 'redirect:/index'

    @Autowired
    AuditService auditService
    @Autowired
    FtpFileProcessor ftpFileProcessor
    @Autowired
    WaterQualityDataCollectionService waterQualityDataCollectionService
    @Autowired
    ProfileDataCollectionService profileDataCollectionService

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/swims')
    String getSwimsData() {
        auditService.audit('JOB', 'Fetch SWIMS Data Manual Request', this.class.simpleName)
        waterQualityDataCollectionService.collectNorthPipeLakeData()
        waterQualityDataCollectionService.collectPipeLakeData()
        profileDataCollectionService.collectNorthPipeLakeData()
        profileDataCollectionService.collectPipeLakeData()
        return REDIRECT_HOME
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/audit/truncate')
    String auditTruncate() {
        auditService.audit('JOB', "audit truncate manual request", this.class.simpleName)
        int count = auditService.truncate()
        log.info("Truncated audit log by ${count} records")
        return REDIRECT_HOME
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/ftp/process')
    String ftpFileProcessor() {
        ftpFileProcessor.processUploadedFiles()
        return REDIRECT_HOME
    }

}
