package com.lake.controller

import com.lake.service.*
import groovy.json.JsonOutput
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
    LocationService locationService
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
        auditService.audit(HttpMethod.GET, '/index', this.class.simpleName)
        model.addAttribute('sites', siteService.getAllSites())
        return 'index'
    }

    @Secured('ROLE_REPORTER')
    @GetMapping('/dataEntry')
    String dataEntry(Model model) {
        auditService.audit(HttpMethod.GET, '/dataEntry', this.class.simpleName)
        model.addAttribute('sites', siteService.getAllSites())
        model.addAttribute('units', unitService.getAllUnits())
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/userMaintenance')
    String userMaintenance() {
        auditService.audit(HttpMethod.GET, '/userMaintenance', this.class.simpleName)
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/locationMaintenance')
    String locationMaintenance(Model model) {
        model.addAttribute('siteOptions', getSites())
        auditService.audit(HttpMethod.GET, '/locationMaintenance', this.class.simpleName)
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/unitMaintenance')
    String unitMaintenance() {
        auditService.audit(HttpMethod.GET, '/unitMaintenance', this.class.simpleName)
        return 'unitMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/dataMaintenance')
    String dataMaintenance(Model model) {
        auditService.audit(HttpMethod.GET, '/dataMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('unitOptions', getUnits())
        model.addAttribute('locationOptions', getLocations())
        return 'dataMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/audit')
    String audit() {
        auditService.audit(HttpMethod.GET, '/audit', this.class.simpleName)
        return 'audit'
    }

    //--------------------------------------------------------------------------------------

    private String getSites() {
        List results = [[id: "-1", name: ""]]
        siteService.getAllSites().each {
            results.add([id: it.id, name: it.description])
        }
        return JsonOutput.toJson(results)
    }

    private String getUnits() {
        List results = [[id: "-1", name: ""]]
        unitService.getAllUnits().each {
            results.add([id: it.id, name: it.longDescription])
        }
        return JsonOutput.toJson(results)
    }

    private String getLocations() {
        List results = [[id: "-1", name: ""]]
        locationService.getAll().each {
            results.add([id: it.id, name: it.description])
        }
        return JsonOutput.toJson(results)
    }

}
