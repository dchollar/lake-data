package com.lake.controller

import com.lake.entity.Document
import com.lake.service.AuditService
import com.lake.service.DocumentService
import com.lake.service.FundingSourceService
import com.lake.service.SiteService
import com.lake.util.ConverterUtil
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@CompileStatic
@Slf4j
@Controller
class PublicPageController {

    private static final String DOCUMENTS = 'documents'
    private static final String FUNDING_SOURCES = 'fundingSources'
    private static final String SITES = 'sites'
    private static final String VERSION = 'version'

    @Autowired
    AuditService auditService
    @Autowired
    BuildProperties buildProperties
    @Autowired
    DocumentService documentService
    @Autowired
    FundingSourceService fundingSourceService
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
        model.addAttribute(DOCUMENTS, JsonOutput.toJson(ConverterUtil.convertDocuments(documents, null)))
        return TemplateName.documentFrame.name()
    }

}
