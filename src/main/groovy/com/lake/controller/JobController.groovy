package com.lake.controller

import com.lake.job.AuditTruncate
import com.lake.job.SwimsDataCollector
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@CompileStatic
@Slf4j
@Controller
class JobController {

    @Autowired
    SwimsDataCollector swimsDataCollector
    @Autowired
    AuditTruncate auditTruncate
    @Autowired
    PageController pageController

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/swims')
    String getSwimsData(Model model, @RequestParam(name = 'currentYear', required = false) Boolean currentYear) {
        String year = currentYear ? SwimsDataCollector.currentYear() : SwimsDataCollector.FIRST_YEAR
        swimsDataCollector.fetchData(year)
        pageController.index(model)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs/audit/truncate')
    String auditTruncate(Model model) {
        auditTruncate.truncate()
        pageController.index(model)
    }

}
