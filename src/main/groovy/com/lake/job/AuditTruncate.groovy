package com.lake.job

import com.lake.service.AuditService
import com.lake.service.ReporterService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@CompileStatic
@Slf4j
@Component
class AuditTruncate {

    private static long DAYS_TO_KEEP = 15
    private static final String REPORTER_USERNAME = 'audit'

    @Autowired
    ReporterService reporterService

    @Autowired
    AuditService auditService

    @Scheduled(cron = "0 0 0 * * *")
    void truncateJob() {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(REPORTER_USERNAME, null)
        SecurityContextHolder.getContext().setAuthentication(token)
        truncate()
    }

    void truncate() {
        auditService.audit('JOB', "audit truncate", this.class.simpleName)
        try {
            int count = auditService.truncate(DAYS_TO_KEEP)
            log.info("Truncated audit log by ${count} records")
        } catch (Exception e) {
            log.error('Issues in truncate job', e)
        }
    }
}
