package com.lake.controller

import com.lake.dto.AuditDto
import com.lake.service.AuditService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@Slf4j
@RestController
class AuditController {

    @Autowired
    AuditService auditService

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/audits', produces = APPLICATION_JSON_VALUE)
    Collection<AuditDto> getAll(@RequestParam(name = 'timezone', required = true) String timezone,
                                @RequestParam(name = 'httpMethod', required = false) String httpMethod,
                                @RequestParam(name = 'endpoint', required = false) String endpoint,
                                @RequestParam(name = 'controller', required = false) String controller,
                                @RequestParam(name = 'reporterName', required = false) String reporterName) {

        //auditService.audit(HttpMethod.GET, "/api/audits?${timezone}", this.class.simpleName)

        AuditDto filter = new AuditDto()
        filter.httpMethod = httpMethod
        filter.endpoint = endpoint
        filter.controller = controller
        filter.reporterName = reporterName

        return auditService.getAll(timezone, filter)
    }
}
