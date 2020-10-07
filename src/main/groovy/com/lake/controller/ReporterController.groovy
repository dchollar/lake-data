package com.lake.controller

import com.lake.dto.ReporterDto
import com.lake.service.ReporterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
class ReporterController {

    @Autowired
    ReporterService service

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/reporters', produces = APPLICATION_JSON_VALUE)
    Collection<ReporterDto> getAll() {
        return service.getAllReporters()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/reporters', produces = APPLICATION_JSON_VALUE)
    ReporterDto create(@RequestBody ReporterDto dto) {
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/reporters/{reporterId}', produces = APPLICATION_JSON_VALUE)
    ReporterDto update(@PathVariable(name = 'reporterId', required = true) Integer reporterId,
                       @RequestBody ReporterDto dto) {
        return service.update(reporterId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/reporters/{reporterId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'reporterId', required = true) Integer reporterId) {
        service.delete(reporterId)
    }


}
