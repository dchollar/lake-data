package com.lake.controller

import com.lake.dto.UnitDto
import com.lake.service.UnitService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
class UnitController {

    @Autowired
    UnitService service

    @GetMapping(value = '/units', produces = APPLICATION_JSON_VALUE)
    Collection<UnitDto> getAll() {
        return service.getAllUnits()
    }

    @GetMapping(value = '/public/api/units/{unitId}', produces = APPLICATION_JSON_VALUE)
    UnitDto getOne(@PathVariable(name = 'unitId', required = true) Integer unitId) {
        return service.getById(unitId)
    }

}
