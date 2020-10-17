package com.lake.controller

import com.lake.service.AuditService
import com.lake.service.ReporterService
import com.lake.service.SiteService
import com.lake.service.UnitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Slf4j
@Controller
class PageController {

    @Autowired
    SiteService siteService
    @Autowired
    UnitService unitService
    @Autowired
    ReporterService reporterService
    @Autowired
    AuditService auditService

    @GetMapping(['/', '/index', '/home'])
    String index(Model model) {
        log.info("ACCESS - view main page")
        auditService.audit(HttpMethod.GET, '/index', this.class.simpleName)
        model.addAttribute('sites', siteService.getAllSites())
        return 'index'
    }

    @Secured('ROLE_REPORTER')
    @GetMapping('/dataEntry')
    String dataEntry(Model model) {
        log.info("ACCESS - view data entry page")
        auditService.audit(HttpMethod.GET, '/dataEntry', this.class.simpleName)
        model.addAttribute('sites', siteService.getAllSites())
        model.addAttribute('units', unitService.getAllUnits())
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/userMaintenance')
    String userMaintenance() {
        log.info("ACCESS - view user maintenance page")
        auditService.audit(HttpMethod.GET, '/userMaintenance', this.class.simpleName)
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/locationMaintenance')
    String locationMaintenance() {
        log.info("ACCESS - view location maintenance page")
        auditService.audit(HttpMethod.GET, '/locationMaintenance', this.class.simpleName)
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/unitMaintenance')
    String unitMaintenance() {
        log.info("ACCESS - view unit maintenance page")
        auditService.audit(HttpMethod.GET, '/unitMaintenance', this.class.simpleName)
        return 'unitMaintenance'
    }

}
