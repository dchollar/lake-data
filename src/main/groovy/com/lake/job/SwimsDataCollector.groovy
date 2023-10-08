package com.lake.job

import com.lake.entity.RoleType
import com.lake.service.AuditService
import com.lake.service.ProfileDataCollectionService
import com.lake.service.WaterQualityDataCollectionService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@CompileStatic
@Slf4j
@Component
class SwimsDataCollector {

    private static final String REPORTER_USERNAME = 'swims'
    private static final String REPORTER_CREDENTIALS = 'credentials'
    private static final List AUTHORITIES = [new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name())]

    @Autowired
    AuditService auditService
    @Autowired
    WaterQualityDataCollectionService waterQualityDataCollectionService
    @Autowired
    ProfileDataCollectionService profileDataCollectionService

    @Scheduled(cron = "0 0 0 1 * *")
    //@Scheduled(cron = "0 * * * * *")
    void fetchData() {
        try {
            final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(REPORTER_USERNAME, REPORTER_CREDENTIALS, AUTHORITIES)
            SecurityContextHolder.getContext().setAuthentication(token)
            fetchDataInternal()
        } catch (Exception e) {
            log.error('Issue processing SWIMS data', e)
            auditService.audit(e)
        }
    }

    private void fetchDataInternal() {
        auditService.audit('JOB', "Fetch SWIMS Data", this.class.simpleName)
        waterQualityDataCollectionService.collectNorthPipeLakeData()
        waterQualityDataCollectionService.collectPipeLakeData()
        profileDataCollectionService.collectNorthPipeLakeData()
        profileDataCollectionService.collectPipeLakeData()
        log.info("Done processing SWIMS Data")
    }

}
