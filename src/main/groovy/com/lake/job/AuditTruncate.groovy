package com.lake.job

import com.lake.service.AuditService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Slf4j
@Component
class AuditTruncate {

    private static long DAYS_TO_KEEP = 15

    @Autowired
    AuditService auditService


    @Scheduled(cron = "0 0 0 * * *")
    void truncate() {
        try {
            int count = auditService.truncate(DAYS_TO_KEEP)
            log.info("Truncated audit log by ${count} records")
        } catch (Exception e) {
            log.error('Issues in truncate job', e)
        }
    }
}
