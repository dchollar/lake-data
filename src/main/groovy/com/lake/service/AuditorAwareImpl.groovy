package com.lake.service

import com.lake.entity.Reporter
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Component

@CompileStatic
@Component
class AuditorAwareImpl implements AuditorAware<Reporter> {

    @Autowired
    ReporterService reporterService

    @Override
    Optional<Reporter> getCurrentAuditor() {
        return Optional.ofNullable(reporterService.getReporter(ReporterService.getUsername()))
    }

}
