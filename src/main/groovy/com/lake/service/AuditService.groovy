package com.lake.service

import com.lake.dto.AuditDto
import com.lake.entity.Audit
import com.lake.entity.Reporter
import com.lake.repository.AuditRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.temporal.ChronoUnit

@CompileStatic
@Service
class AuditService {
    @Autowired
    AuditRepository repository
    @Autowired
    ReporterService reporterService

    @Transactional
    void audit(String method, String endpoint, String controller, Reporter reporter = null) {
        Audit audit = new Audit()
        audit.created = Instant.now()
        audit.endpoint = endpoint
        audit.controller = controller
        audit.httpMethod = method
        audit.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())

        repository.saveAndFlush(audit)
    }

    @Secured('ROLE_ADMIN')
    Collection<AuditDto> getAll(String timezone, AuditDto filter) {
        Collection<Audit> entities = repository.findAll()
        ConverterUtil.convertAudits(entities, timezone, filter)
    }

    @Transactional
    int truncate(long days) {
        List<Audit> audits = repository.findAllByCreatedLessThan(Instant.now().minus(days, ChronoUnit.DAYS))
        repository.deleteAll(audits)
        return audits.size()
    }

}
