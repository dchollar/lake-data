package com.lake.controller

import com.lake.entity.CharacteristicType
import com.lake.entity.Document
import com.lake.service.*
import com.lake.util.ConverterUtil
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@CompileStatic
@Slf4j
@Controller
class PageController {

    private static final String CHARACTERISTICS = 'characteristics'
    private static final String DOCUMENTS = 'documents'
    private static final String FUNDING_SOURCES = 'fundingSources'
    private static final String LOCATIONS = 'locations'
    private static final String SITES = 'sites'
    private static final String VERSION = 'version'

    @Autowired
    AuditService auditService
    @Autowired
    BuildProperties buildProperties
    @Autowired
    CharacteristicService characteristicService
    @Autowired
    DocumentService documentService
    @Autowired
    FundingSourceService fundingSourceService
    @Autowired
    LocationService locationService
    @Autowired
    ReporterService reporterService
    @Autowired
    SiteService siteService

    @GetMapping(['favicon.ico'])
    @ResponseBody
    String favicon() {
        return "forward:/public/ico/favicon.ico"
    }

    @GetMapping(['/', '/index', '/home'])
    String index(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/index', this.class.simpleName)
        model.addAttribute(SITES, siteService.getAll())
        model.addAttribute(FUNDING_SOURCES, fundingSourceService.getAll())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.index.name()
    }

    @GetMapping('/public/page/dataFrame')
    String dataFrame(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataFrame', this.class.simpleName)
        model.addAttribute(SITES, siteService.getAll())
        model.addAttribute(FUNDING_SOURCES, fundingSourceService.getAll())
        return TemplateName.dataFrame.name()
    }

    @GetMapping('/public/page/documents')
    String documents(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/public/page/documents', this.class.simpleName)
        model.addAttribute(SITES, siteService.getSitesWithDocuments())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.documents.name()
    }

    @GetMapping('/public/page/privacy')
    String privacyStatement(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/public/page/privacy', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.privacy.name()
    }

    @GetMapping('/public/page/test')
    String iframeTest() {
        // TODO this is just temporary
        return 'iframeTest'
    }

    @GetMapping('/public/page/documentFrame')
    String documentFrame(Model model, @RequestParam(name = 'category', required = true) String category) {
        Collection<Document> documents = documentService.findDocuments(category)
        // TODO not sure I want to do latest document
        Document latestDocument = documents.sort { a, b -> a.created <=> b.created }.first()
        model.addAttribute(DOCUMENTS, JsonOutput.toJson(ConverterUtil.convertDocuments(documents, null)))
        // could use the document id to build a url on the page.
        model.addAttribute('latestDocumentId', latestDocument.id)
        return TemplateName.documentFrame.name()
    }

    //--------------------------------------------------------------------------------------

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/audit')
    String audit(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/audit', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.audit.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/characteristicLocationMaintenance')
    String characteristicLocationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/characteristicLocationMaintenance', this.class.simpleName)
        model.addAttribute(SITES, getSites())
        model.addAttribute(CHARACTERISTICS, getWaterCharacteristics())
        model.addAttribute(LOCATIONS, getLocations())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.characteristicLocationMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/characteristicMaintenance')
    String characteristicMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/characteristicMaintenance', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.characteristicMaintenance.name()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_REPORTER'])
    @GetMapping('/page/dataEntry')
    String dataEntry(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/dataEntry', this.class.simpleName)
        model.addAttribute(SITES, siteService.getAll())
        model.addAttribute(FUNDING_SOURCES, fundingSourceService.getAll())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.dataEntry.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/dataMaintenance')
    String dataMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/dataMaintenance', this.class.simpleName)
        model.addAttribute(SITES, getSites())
        model.addAttribute(CHARACTERISTICS, getCharacteristics())
        model.addAttribute(LOCATIONS, getLocations())
        model.addAttribute(FUNDING_SOURCES, getFundingSources())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.dataMaintenance.name()
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @GetMapping('/page/documentMaintenance')
    String documentMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/documentMaintenance', this.class.simpleName)
        model.addAttribute(SITES, getSites())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.documentMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/fundingSourceMaintenance')
    String fundingSourceMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/fundingSourceMaintenance', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.fundingSourceMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/jobs')
    String jobs(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/jobs', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.jobMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/locationMaintenance')
    String locationMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/locationMaintenance', this.class.simpleName)
        model.addAttribute(SITES, getSites())
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.locationMaintenance.name()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping('/page/userMaintenance')
    String userMaintenance(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/page/userMaintenance', this.class.simpleName)
        model.addAttribute(VERSION, buildProperties.getVersion())
        return TemplateName.userMaintenance.name()
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
