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
import java.time.temporal.ChronoUnit

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
        audit.reporter = reporterService.getReporter(ReporterService.getUsername())

        repository.saveAndFlush(audit)
    }

    Collection<AuditDto> getAll(String timezone, filter) {
        ConverterUtil.convertAudits(repository.findAll(), timezone, filter)
    }

    @Transactional
    int truncate(long days) {
        List<Audit> audits = repository.findAllByCreatedLessThan(Instant.now().minus(days, ChronoUnit.DAYS))
        repository.deleteAll(audits)
        return audits.size()
    }

}
