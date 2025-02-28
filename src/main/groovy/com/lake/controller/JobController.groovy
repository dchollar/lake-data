package com.lake.controller


import com.lake.job.FtpFileProcessor
import com.lake.service.AuditService
import com.lake.service.SwimsProfileDataCollectionService
import com.lake.service.SwimsWaterQualityDataCollectionService
import com.lake.service.WexProfileDataCollectionService
import com.lake.service.WexWaterQualityDataCollectionService
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
    SwimsProfileDataCollectionService swimsProfileDataCollectionService
    @Autowired
    SwimsWaterQualityDataCollectionService swimsWaterQualityDataCollectionService
    @Autowired
    WexProfileDataCollectionService wexProfileDataCollectionService
    @Autowired
    WexWaterQualityDataCollectionService wexWaterQualityDataCollectionService


    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/swims')
    String getSwimsData() {
        auditService.audit('JOB', 'Fetch SWIMS Data Manual Request', this.class.simpleName)
        swimsWaterQualityDataCollectionService.collectNorthPipeLakeData()
        swimsWaterQualityDataCollectionService.collectPipeLakeData()
        swimsProfileDataCollectionService.collectNorthPipeLakeData()
        swimsProfileDataCollectionService.collectPipeLakeData()

        wexWaterQualityDataCollectionService.collectNorthPipeLakeData()
        wexWaterQualityDataCollectionService.collectPipeLakeData()
        wexProfileDataCollectionService.collectNorthPipeLakeData()
        wexProfileDataCollectionService.collectPipeLakeData()
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
