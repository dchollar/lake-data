package com.lake.controller

import com.lake.entity.CharacteristicType
import com.lake.service.*
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
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
    BuildProperties buildProperties

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
        return TemplateName.index.name()
    }

    @GetMapping('/public/page/documents')
    String documents(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/public/page/documents', this.class.simpleName)
        model.addAttribute('sites', siteService.getSitesWithDocuments())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.documents.name()
    }

    @GetMapping('/public/page/privacy')
    String privacyStatement(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/public/page/privacy', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.privacy.name()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_REPORTER'])
    @GetMapping('/page/dataEntry')
    String dataEntry(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/dataEntry', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('fundingSources', fundingSourceService.getAll())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.dataEntry.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/userMaintenance')
    String userMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/userMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.userMaintenance.name()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @GetMapping('/page/documentMaintenance')
    String documentMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/documentMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.documentMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/locationMaintenance')
    String locationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/locationMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.locationMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/characteristicMaintenance')
    String characteristicMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/characteristicMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.characteristicMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/characteristicLocationMaintenance')
    String characteristicLocationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/characteristicLocationMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getWaterCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.characteristicLocationMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/dataMaintenance')
    String dataMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/dataMaintenance', this.class.simpleName)
        model.addAttribute('siteOptions', getSites())
        model.addAttribute('characteristicOptions', getCharacteristics())
        model.addAttribute('locationOptions', getLocations())
        model.addAttribute('fundingSourceOptions', getFundingSources())
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.dataMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/fundingSourceMaintenance')
    String fundingSourceMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/fundingSourceMaintenance', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.fundingSourceMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/audit')
    String audit(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/audit', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.audit.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/jobs')
    String jobs(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/jobs', this.class.simpleName)
        model.addAttribute('version', buildProperties.getVersion())
        return TemplateName.jobMaintenance.name()
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
