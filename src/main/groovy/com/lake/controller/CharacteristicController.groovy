package com.lake.controller

import com.lake.dto.CharacteristicDto
import com.lake.entity.CharacteristicType
import com.lake.service.AuditService
import com.lake.service.CharacteristicService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@CompileStatic
@Slf4j
@RestController
class CharacteristicController {

    @Autowired
    CharacteristicService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/characteristics/{characteristicId}', produces = APPLICATION_JSON_VALUE)
    CharacteristicDto getOne(@PathVariable(name = 'characteristicId', required = true) Integer characteristicId) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/characteristics/${characteristicId}", this.class.simpleName)
        return service.getById(characteristicId)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_REPORTER'])
    @GetMapping(value = '/api/characteristics', produces = APPLICATION_JSON_VALUE)
    Collection<CharacteristicDto> getAll(@RequestParam(name = 'siteId', required = false) Integer siteId) {
        auditService.audit(HttpMethod.GET.name(), '/api/characteristics', this.class.simpleName)
        if (siteId) {
            service.getCharacteristicsBySiteForDataEntry(siteId)
        } else {
            return service.getAll()
        }
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/characteristicTypes', produces = APPLICATION_JSON_VALUE)
    List getAllCharacteristicTypes() {
        auditService.audit(HttpMethod.GET.name(), '/api/characteristicTypes', this.class.simpleName)
        List types = [[id: "-1", name: ""]]
        CharacteristicType.values().each {
            types.add([id: it.name(), name: it.name()])
        }
        return types
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/characteristics', produces = APPLICATION_JSON_VALUE)
    CharacteristicDto create(@RequestBody CharacteristicDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/characteristics', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/characteristics/{characteristicId}', produces = APPLICATION_JSON_VALUE)
    CharacteristicDto update(@PathVariable(name = 'characteristicId', required = true) Integer characteristicId,
                             @RequestBody CharacteristicDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/characteristics/${characteristicId}", this.class.simpleName)
        return service.update(characteristicId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/characteristics/{characteristicId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'characteristicId', required = true) Integer characteristicId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/characteristics/${characteristicId}", this.class.simpleName)
        service.delete(characteristicId)
    }

}
