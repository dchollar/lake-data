package com.lake.controller

import com.lake.job.AuditTruncate
import com.lake.job.SwimsDataCollector
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Slf4j
@Controller
class JobController {

    @Autowired
    SwimsDataCollector swimsDataCollector
    @Autowired
    AuditTruncate auditTruncate

    @GetMapping('/job/swims')
    String getSwimsData() {
        swimsDataCollector.fetchData(SwimsDataCollector.FIRST_YEAR)
        return 'index'
    }

    @GetMapping('/job/audit/truncate')
    String auditTruncate() {
        auditTruncate.truncate()
        return 'index'
    }

}
