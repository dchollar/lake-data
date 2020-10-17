package com.lake.controller

import com.lake.dto.UnitDto
import com.lake.service.AuditService
import com.lake.service.UnitService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class UnitController {

    @Autowired
    UnitService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto getOne(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        log.info("ACCESS - get unit by id")
        auditService.audit(HttpMethod.GET, "/public/api/units/${unitId}", this.class.simpleName)
        return service.getById(unitId)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    Collection<UnitDto> getAll() {
        log.info("ACCESS - get all units")
        auditService.audit(HttpMethod.GET, '/api/units', this.class.simpleName, true)
        return service.getAllUnits()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    UnitDto create(@RequestBody UnitDto dto) {
        log.info("ACCESS - create new unit")
        auditService.audit(HttpMethod.POST, '/api/units', this.class.simpleName, true)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto update(@PathVariable(name = 'unitId', required = true) Integer unitId,
                   @RequestBody UnitDto dto) {
        log.info("ACCESS - update unit")
        auditService.audit(HttpMethod.PUT, "/api/units/${unitId}", this.class.simpleName, true)
        return service.update(unitId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        log.info("ACCESS - delete unit")
        auditService.audit(HttpMethod.DELETE, "/api/units/${unitId}", this.class.simpleName, true)
        service.delete(unitId)
    }

}
