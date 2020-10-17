package com.lake.controller

import com.lake.dto.LocationDto
import com.lake.service.AuditService
import com.lake.service.LocationService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class LocationController {

    @Autowired
    LocationService service
    @Autowired
    AuditService auditService

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/locations', produces = APPLICATION_JSON_VALUE)
    Collection<LocationDto> getAll() {
        log.info("ACCESS - get all locations")
        auditService.audit(HttpMethod.GET, '/api/locations', this.class.simpleName)
        return service.getAll()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/locations', produces = APPLICATION_JSON_VALUE)
    LocationDto create(@RequestBody LocationDto dto) {
        log.info("ACCESS - create new location")
        auditService.audit(HttpMethod.POST, '/api/locations', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/locations/{locationId}', produces = APPLICATION_JSON_VALUE)
    LocationDto update(@PathVariable(name = 'locationId', required = true) Integer locationId,
                       @RequestBody LocationDto dto) {
        log.info("ACCESS - update location")
        auditService.audit(HttpMethod.PUT, "/api/locations/${locationId}", this.class.simpleName)
        return service.update(locationId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/locations/{locationId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'locationId', required = true) Integer locationId) {
        log.info("ACCESS - delete location")
        auditService.audit(HttpMethod.DELETE, "/api/locations/${locationId}", this.class.simpleName)
        service.delete(locationId)
    }


}
