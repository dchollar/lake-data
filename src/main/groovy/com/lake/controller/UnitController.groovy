package com.lake.controller

import com.lake.dto.UnitDto
import com.lake.service.UnitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
class UnitController {

    @Autowired
    UnitService service

    @GetMapping(value = '/public/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto getOne(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        return service.getById(unitId)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    Collection<UnitDto> getAll() {
        return service.getAllUnits()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/units', produces = APPLICATION_JSON_VALUE)
    UnitDto create(@RequestBody UnitDto dto) {
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto update(@PathVariable(name = 'unitId', required = true) Integer unitId,
                   @RequestBody UnitDto dto) {
        return service.update(unitId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        service.delete(unitId)
    }

}
