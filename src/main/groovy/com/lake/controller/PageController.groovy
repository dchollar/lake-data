package com.lake.controller

import com.lake.entity.CharacteristicType
import com.lake.service.*
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@CompileStatic
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
    @Autowired
    private BuildProperties buildProperties

    @GetMapping(['favicon.ico'])
    @ResponseBody
    String favicon() {
        return "forward:/ico/favicon.ico"
    }

    @GetMapping(['/', '/index', '/home'])
    String index(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/index', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('fundingSources', fundingSourceService.getAll())
        model.addAttribute('version', buildProperties.getVersion())
        return 'index'
    }

    @GetMapping('/documents')
    String documents(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/public/documents', this.class.simpleName)
        model.addAttribute('sites', siteService.getSitesWithDocuments())
        model.addAttribute('version', buildProperties.getVersion())
        return 'documents'
    }

    @Secured('ROLE_REPORTER')
    @GetMapping('/dataEntry')
    String dataEntry(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataEntry', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('characteristics', characteristicService.getAll())
        model.addAttribute('fundingSources', fundingSourceService.getAll())
        model.addAttribute('version', buildProperties.getVersion())
        return 'dataEntry'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/userMaintenance')
    String userMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/userMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return 'userMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/documentMaintenance')
    String documentMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/documentMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('version', buildProperties.getVersion())
        return 'documentMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/locationMaintenance')
    String locationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/locationMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('version', buildProperties.getVersion())
        return 'locationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/characteristicMaintenance')
    String characteristicMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/characteristicMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return 'characteristicMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/characteristicLocationMaintenance')
    String characteristicLocationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/characteristicLocationMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getWaterCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        model.addAttribute('version', buildProperties.getVersion())
        return 'characteristicLocationMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/dataMaintenance')
    String dataMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        model.addAttribute('fundingSourceOptions', getFundingSources())
        model.addAttribute('version', buildProperties.getVersion())
        return 'dataMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/fundingSourceMaintenance')
    String fundingSourceMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/fundingSourceMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return 'fundingSourceMaintenance'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/audit')
    String audit(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/audit', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return 'audit'
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/jobs')
    String jobs(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/jobs', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return 'jobMaintenance'
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

    private String getFundingSources() {
        List results = [[id: -1, name: '']]
        fundingSourceService.getAll().each {
            results.add([id: it.id, name: it.name])
        }
        return JsonOutput.toJson(results)
    }

}
