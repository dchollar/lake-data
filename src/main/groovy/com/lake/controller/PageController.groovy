package com.lake.controller

import com.lake.service.SiteService
import com.lake.service.UnitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class PageController {

    @Autowired
    SiteService siteService
    @Autowired
    UnitService unitService

    @RequestMapping(['/', '/index', '/home'])
    String index(Model model) {
        model.addAttribute('sites', siteService.getAllSites())
        model.addAttribute('units', unitService.getAllUnits())
        return 'index'
    }

    @Secured('ROLE_REPORTER')
    @RequestMapping('/dataEntry')
    String dataEntry() {
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @RequestMapping('/users')
    String userMaintenance() {
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @RequestMapping('/locations')
    String locationrMaintenance() {
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @RequestMapping('/units')
    String unitMaintenance() {
        return 'unitMaintenance'
    }

}
