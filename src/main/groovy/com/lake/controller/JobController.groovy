package com.lake.controller

import com.lake.job.AuditTruncate
import com.lake.job.FtpFileProcessor
import com.lake.service.AuditService
import com.lake.service.ProfileDataCollectionService
import com.lake.service.WaterQualityDataCollectionService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@CompileStatic
@Slf4j
@Controller
class JobController {

    @Autowired
    AuditService auditService
    @Autowired
    AuditTruncate auditTruncate
    @Autowired
    FtpFileProcessor ftpFileProcessor
    @Autowired
    PageController pageController
    @Autowired
    WaterQualityDataCollectionService waterQualityDataCollectionService
    @Autowired
    ProfileDataCollectionService profileDataCollectionService

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/swims')
    String getSwimsData(Model model) {
        auditService.audit('JOB', "Fetch SWIMS Data Manual Request", this.class.simpleName)
        waterQualityDataCollectionService.collectNorthPipeLakeData()
        waterQualityDataCollectionService.collectPipeLakeData()
        profileDataCollectionService.collectNorthPipeLakeData()
        profileDataCollectionService.collectPipeLakeData()
        pageController.index(model)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/audit/truncate')
    String auditTruncate(Model model) {
        auditTruncate.truncate()
        pageController.index(model)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/ftp/process')
    String ftpFileProcessor(Model model) {
        ftpFileProcessor.processUploadedFiles()
        pageController.index(model)
    }

}
