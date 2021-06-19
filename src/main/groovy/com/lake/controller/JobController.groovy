package com.lake.controller

import com.lake.job.AuditTruncate
import com.lake.job.SwimsDataCollector
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@CompileStatic
@Slf4j
@Controller
class JobController {

    @Autowired
    SwimsDataCollector swimsDataCollector
    @Autowired
    AuditTruncate auditTruncate

    @GetMapping('/jobs/swims')
    String getSwimsData() {
        swimsDataCollector.fetchData(SwimsDataCollector.FIRST_YEAR)
        return 'index'
    }

    @GetMapping('/jobs/audit/truncate')
    String auditTruncate() {
        auditTruncate.truncate()
        return 'index'
    }

}
