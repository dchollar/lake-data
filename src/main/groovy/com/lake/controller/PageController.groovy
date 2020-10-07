package com.lake.controller


import com.lake.service.ReporterService
import com.lake.service.SiteService
import com.lake.service.UnitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PageController {

    @Autowired
    SiteService siteService
    @Autowired
    UnitService unitService
    @Autowired
    ReporterService reporterService

    @GetMapping(['/', '/index', '/home'])
    String index(Model model) {
        model.addAttribute('sites', siteService.getAllSites())
        return 'index'
    }

    @Secured('ROLE_REPORTER')
    @GetMapping('/dataEntry')
    String dataEntry(Model model) {
        model.addAttribute('sites', siteService.getAllSites())
        model.addAttribute('units', unitService.getAllUnits())
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/userMaintenance')
    String userMaintenance() {
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/locationMaintenance')
    String locationMaintenance() {
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/unitMaintenance')
    String unitMaintenance() {
        return 'unitMaintenance'
    }

}
