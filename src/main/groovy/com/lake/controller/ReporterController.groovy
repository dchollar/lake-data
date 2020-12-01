package com.lake.controller

import com.lake.dto.ReporterDto
import com.lake.service.AuditService
import com.lake.service.ReporterService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class ReporterController {

    @Autowired
    ReporterService service
    @Autowired
    AuditService auditService

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/reporters', produces = APPLICATION_JSON_VALUE)
    Collection<ReporterDto> getAll() {
        log.info("ACCESS - get all reporters")
        auditService.audit(HttpMethod.GET.name(), '/api/reporters', this.class.simpleName)
        return service.getAllReporters()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/reporters', produces = APPLICATION_JSON_VALUE)
    ReporterDto create(@RequestBody ReporterDto dto) {
        log.info("ACCESS - create new reporter")
        auditService.audit(HttpMethod.POST.name(), '/api/reporters', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/reporters/{reporterId}', produces = APPLICATION_JSON_VALUE)
    ReporterDto update(@PathVariable(name = 'reporterId', required = true) Integer reporterId,
                       @RequestBody ReporterDto dto) {
        log.info("ACCESS - update reporter")
        auditService.audit(HttpMethod.PUT.name(), "/api/reporters/${reporterId}", this.class.simpleName)
        return service.update(reporterId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/reporters/{reporterId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'reporterId', required = true) Integer reporterId) {
        log.info("ACCESS - delete reporter")
        auditService.audit(HttpMethod.DELETE.name(), "/api/reporters/${reporterId}", this.class.simpleName)
        service.delete(reporterId)
    }


}
