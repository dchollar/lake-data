package com.lake.controller

import com.lake.dto.LocationDto
import com.lake.dto.SiteDto
import com.lake.dto.UnitDto
import com.lake.service.AuditService
import com.lake.service.LocationService
import com.lake.service.SiteService
import com.lake.service.UnitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class SiteController {

    @Autowired
    SiteService service
    @Autowired
    UnitService unitService
    @Autowired
    LocationService locationService
    @Autowired
    AuditService auditService

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/sites', produces = APPLICATION_JSON_VALUE)
    Collection<SiteDto> getAll() {
        log.info("ACCESS - get all sites")
        auditService.audit(HttpMethod.GET.name(), '/api/sites', this.class.simpleName)
        return service.getAllSites()
    }

    @GetMapping(value = '/public/api/sites/{siteId}/locations', produces = APPLICATION_JSON_VALUE)
    Collection<LocationDto> getSiteLocations(@PathVariable(name = 'siteId', required = true) Integer siteId) {
        log.info("ACCESS - get locations by site")
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}/locations", this.class.simpleName)
        return locationService.getLocationsBySite(siteId)
    }

    @GetMapping(value = '/public/api/sites/{siteId}/units', produces = APPLICATION_JSON_VALUE)
    Collection<UnitDto> getSiteUnits(@PathVariable(name = 'siteId', required = true) Integer siteId) {
        log.info("ACCESS - get units by site")
        auditService.audit(HttpMethod.GET.name(), "/public/api/sites/${siteId}/units", this.class.simpleName)
        return unitService.getUnitsBySite(siteId)
    }

}
