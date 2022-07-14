package com.lake.service

import com.lake.dto.AuditDto
import com.lake.entity.Audit
import com.lake.entity.Reporter
import com.lake.repository.AuditRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Instant
import java.time.temporal.ChronoUnit

@CompileStatic
@Transactional
@Service
class AuditService {
    @Autowired
    AuditRepository repository
    @Autowired
    ReporterService reporterService

    void audit(String method, String endpoint, String controller, Reporter reporter = null) {
        Audit audit = new Audit()
        audit.created = Instant.now()
        audit.endpoint = endpoint?.take(100)
        audit.controller = controller?.take(45)
        audit.httpMethod = method?.take(10)
        audit.reporter = reporter ? reporter : reporterService.getReporter(ReporterService.getUsername())

        repository.saveAndFlush(audit)
    }

    void audit(Exception e) {
        Throwable rootCause = ExceptionUtils.getRootCause(e)
        audit('ERROR', rootCause.getMessage(), rootCause.getClass().getSimpleName())
    }

    @Secured('ROLE_ADMIN')
    Collection<AuditDto> getAll(String timezone, AuditDto filter) {
        Collection<Audit> entities = repository.findAll()
        ConverterUtil.convertAudits(entities, timezone, filter)
    }

    int truncate(long days) {
        List<Audit> audits = repository.findAllByCreatedLessThan(Instant.now().minus(days, ChronoUnit.DAYS))
        if (audits && !audits.empty) {
            repository.deleteAll(audits)
        }
        return audits.size()
    }

}
