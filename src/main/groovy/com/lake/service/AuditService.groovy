package com.lake.service

import com.lake.dto.AuditDto
import com.lake.entity.Audit
import com.lake.repository.AuditRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant

@Service
class AuditService {
    @Autowired
    AuditRepository repository
    @Autowired
    ReporterService reporterService

    @Transactional
    void audit(HttpMethod method, String endpoint, String controller) {
        Audit audit = new Audit()
        audit.created = Instant.now()
        audit.endpoint = endpoint
        audit.controller = controller
        audit.httpMethod = method.name()
        audit.reporter = reporterService.reporter

        repository.saveAndFlush(audit)
    }

    Collection<AuditDto> getAll(String timezone, filter) {
        ConverterUtil.convertAudits(repository.findAll(), timezone, filter)
    }

}
