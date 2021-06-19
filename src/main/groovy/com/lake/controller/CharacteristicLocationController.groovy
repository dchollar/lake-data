package com.lake.controller

import com.lake.dto.CharacteristicLocationDto
import com.lake.service.AuditService
import com.lake.service.CharacteristicLocationService
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
class CharacteristicLocationController {

    @Autowired
    CharacteristicLocationService service
    @Autowired
    AuditService auditService

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/characteristicLocations', produces = APPLICATION_JSON_VALUE)
    Collection<CharacteristicLocationDto> getAll() {
        auditService.audit(HttpMethod.GET.name(), '/api/characteristicLocations', this.class.simpleName)
        return service.getAll()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/characteristicLocations', produces = APPLICATION_JSON_VALUE)
    CharacteristicLocationDto create(@RequestBody CharacteristicLocationDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/characteristicLocations', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/characteristicLocations/{id}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'id', required = true) Integer id) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/characteristicLocations/${id}", this.class.simpleName)
        service.delete(id)
    }


}
