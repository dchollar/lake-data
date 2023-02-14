package com.lake.controller

import com.lake.dto.CharacteristicDto
import com.lake.dto.DocumentDto
import com.lake.dto.LocationDto
import com.lake.dto.SiteDto
import com.lake.service.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@CompileStatic
@Slf4j
@RestController
class SiteController {

    @Autowired
    SiteService service
    @Autowired
    CharacteristicService characteristicService
    @Autowired
    LocationService locationService
    @Autowired
    AuditService auditService
    @Autowired
    DocumentService documentService

    @GetMapping(value = '/public/api/sites/{siteId}', produces = APPLICATION_JSON_VALUE)
    SiteDto getSite(@PathVariable(name = 'siteId', required = true) Integer siteId) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}", this.class.simpleName)
        return service.get(siteId)
    }

    @GetMapping(value = '/public/api/sites/{siteId}/locations', produces = APPLICATION_JSON_VALUE)
    Collection<LocationDto> getSiteLocations(@PathVariable(name = 'siteId', required = true) Integer siteId,
                                             @RequestParam(name = 'characteristicId', required = false) Integer characteristicId,
                                             @RequestParam(name = 'restricted', required = false) Boolean restricted) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}/locations?characterisitcId=${characteristicId}?restricted=${restricted}", this.class.simpleName)
        if (characteristicId) {
            return locationService.getLocationsBySite(siteId, characteristicId, restricted)
        } else {
            return locationService.getLocationsBySite(siteId)
        }
    }

    @GetMapping(value = '/public/api/sites/{siteId}/characteristics', produces = APPLICATION_JSON_VALUE)
    Collection<CharacteristicDto> getSiteCharacteristics(@PathVariable(name = 'siteId', required = true) Integer siteId) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}/characteristics", this.class.simpleName)
        return characteristicService.getCharacteristicsBySite(siteId)
    }

    @GetMapping(value = '/public/api/sites/{siteId}/documents', produces = APPLICATION_JSON_VALUE)
    Collection<DocumentDto> search(@PathVariable(name = 'siteId', required = true) Integer siteId,
                                   @RequestParam(name = 'searchWord', required = false) String searchWord,
                                   @RequestParam(name = 'category', required = false) String category,
                                   @RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}/documents/search?searchWord=${searchWord} category=${category}", this.class.simpleName)
        return documentService.findDocuments(siteId, searchWord, category, timezone)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/sites', produces = APPLICATION_JSON_VALUE)
    Collection<SiteDto> getAll() {
        log.info("ACCESS - get all sites")
        auditService.audit(HttpMethod.GET.name(), '/api/sites', this.class.simpleName)
        return service.getAll()
    }
}
