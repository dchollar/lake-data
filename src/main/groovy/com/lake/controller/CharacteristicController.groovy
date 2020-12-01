package com.lake.controller

import com.lake.dto.UnitDto
import com.lake.entity.UnitType
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
class CharacteristicController {

    @Autowired
    UnitService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto getOne(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/units/${unitId}", this.class.simpleName)
        return service.getById(unitId)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    Collection<UnitDto> getAll() {
        auditService.audit(HttpMethod.GET.name(), '/api/units', this.class.simpleName)
        return service.getAllUnits()
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/unitTypes', produces = APPLICATION_JSON_VALUE)
    List getAllUnitTypes() {
        auditService.audit(HttpMethod.GET.name(), '/api/unitTypes', this.class.simpleName)
        List unitTypes = [[id: "-1", name: ""]]
        UnitType.values().each {
            unitTypes.add([id: it.name(), name: it.name()])
        }
        return unitTypes
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    UnitDto create(@RequestBody UnitDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/units', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto update(@PathVariable(name = 'unitId', required = true) Integer unitId,
                   @RequestBody UnitDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/units/${unitId}", this.class.simpleName)
        return service.update(unitId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/units/${unitId}", this.class.simpleName)
        service.delete(unitId)
    }

}
