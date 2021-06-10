package com.lake.controller


import com.lake.entity.CharacteristicType
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
    CharacteristicService characteristicService
    @Autowired
    ReporterService reporterService
    @Autowired
    AuditService auditService
    @Autowired
    FundingSourceService fundingSourceService

    @GetMapping(['/', '/index', '/home'])
    String index(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/index', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('fundingSources', fundingSourceService.getAll())
        return 'index'
    }

    @GetMapping('/documents')
    String documents(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/documents', this.class.simpleName)
        model.addAttribute('sites', siteService.getSitesWithDocuments())
        return 'documents'
    }

    @Secured('ROLE_REPORTER')
    @GetMapping('/dataEntry')
    String dataEntry(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataEntry', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('characteristics', characteristicService.getAll())
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/userMaintenance')
    String userMaintenance() {
        auditService.audit(HttpMethod.GET.name(), '/userMaintenance', this.class.simpleName)
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/documentMaintenance')
    String documentMaintenance(Model model) {
        model.addAttribute('siteOptions', getSites())
        auditService.audit(HttpMethod.GET.name(), '/documentMaintenance', this.class.simpleName)
        return 'documentMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/locationMaintenance')
    String locationMaintenance(Model model) {
        model.addAttribute('siteOptions', getSites())
        auditService.audit(HttpMethod.GET.name(), '/locationMaintenance', this.class.simpleName)
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/characteristicMaintenance')
    String characteristicMaintenance() {
        auditService.audit(HttpMethod.GET.name(), '/characteristicMaintenance', this.class.simpleName)
        return 'characteristicMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/characteristicLocationMaintenance')
    String characteristicLocationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/characteristicLocationMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getWaterCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        return 'characteristicLocationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/dataMaintenance')
    String dataMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        return 'dataMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/fundingSourceMaintenance')
    String fundingSourceMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/fundingSourceMaintenance', this.class.simpleName)
        return 'fundingSourceMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/audit')
    String audit() {
        auditService.audit(HttpMethod.GET.name(), '/audit', this.class.simpleName)
        return 'audit'
    }

    //--------------------------------------------------------------------------------------

    private String getSites() {
        List results = [[id: -1, name: '']]
        siteService.getAll().each {
            results.add([id: it.id, name: it.description])
        }
        return JsonOutput.toJson(results)
    }

    private String getCharacteristics() {
        List results = [[id: -1, name: '']]
        characteristicService.getAll().each {
            results.add([id: it.id, name: it.description])
        }
        return JsonOutput.toJson(results)
    }

    private String getWaterCharacteristics() {
        List results = [[id: -1, name: '']]
        characteristicService.getAll().each {
            if (it.type == CharacteristicType.WATER) {
                results.add([id: it.id, name: it.description])
            }
        }
        return JsonOutput.toJson(results)
    }

    private String getLocations() {
        List results = [[id: -1, name: '']]
        locationService.getAll().each {
            results.add([id: it.id, name: it.description])
        }
        return JsonOutput.toJson(results)
    }

}
