package com.lake.controller

import com.lake.dto.FundingSourceDto
import com.lake.service.AuditService
import com.lake.service.FundingSourceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@CompileStatic
@Slf4j
@RestController
class FundingSourceController {

    @Autowired
    FundingSourceService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/fundingSources', produces = APPLICATION_JSON_VALUE)
    Collection<FundingSourceDto> getAll() {
        return service.getAll()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/fundingSources', produces = APPLICATION_JSON_VALUE)
    FundingSourceDto create(@RequestBody FundingSourceDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/fundingSources', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/fundingSources/{fundingSourceId}', produces = APPLICATION_JSON_VALUE)
    FundingSourceDto update(@PathVariable(name = 'fundingSourceId', required = true) Integer fundingSourceId,
                            @RequestBody FundingSourceDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/fundingSources/${fundingSourceId}", this.class.simpleName)
        return service.update(fundingSourceId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/fundingSources/{fundingSourceId}')
    void delete(@PathVariable(name = 'fundingSourceId', required = true) Integer fundingSourceId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/fundingSources/${fundingSourceId}", this.class.simpleName)
        service.delete(fundingSourceId)
    }

}