package com.lake.controller

import com.lake.dto.LocationDto
import com.lake.dto.SiteDto
import com.lake.service.SiteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
class SiteController {

    @Autowired
    SiteService service

    @GetMapping(value = '/api/sites', produces = APPLICATION_JSON_VALUE)
    Collection<SiteDto> getAll() {
        return service.getAllSites()
    }

    @GetMapping(value = '/public/api/sites/{siteId}/locations', produces = APPLICATION_JSON_VALUE)
    Collection<LocationDto> getSiteLocations(@PathVariable(name = 'siteId', required = true) Integer siteId) {
        return service.getLocations(siteId)
    }


}
