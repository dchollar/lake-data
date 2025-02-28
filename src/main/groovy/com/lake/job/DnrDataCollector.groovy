package com.lake.job

import com.lake.entity.RoleType
import com.lake.service.AuditService
import com.lake.service.SwimsProfileDataCollectionService
import com.lake.service.SwimsWaterQualityDataCollectionService
import com.lake.service.WexProfileDataCollectionService
import com.lake.service.WexWaterQualityDataCollectionService
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
class DnrDataCollector {

    private static final String REPORTER_USERNAME = 'swims'
    private static final String REPORTER_CREDENTIALS = 'credentials'
    private static final List AUTHORITIES = [new SimpleGrantedAuthority(RoleType.ROLE_ADMIN.name())]

    @Autowired
    AuditService auditService
    @Autowired
    SwimsProfileDataCollectionService swimsProfileDataCollectionService
    @Autowired
    SwimsWaterQualityDataCollectionService swimsWaterQualityDataCollectionService
    @Autowired
    WexProfileDataCollectionService wexProfileDataCollectionService
    @Autowired
    WexWaterQualityDataCollectionService wexWaterQualityDataCollectionService

    //@Scheduled(cron = '0 0 0 1 * *')
    @Scheduled(cron = '0 * * * * *')
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

//        swimsWaterQualityDataCollectionService.collectNorthPipeLakeData()
//        swimsWaterQualityDataCollectionService.collectPipeLakeData()
//        swimsProfileDataCollectionService.collectNorthPipeLakeData()
//        swimsProfileDataCollectionService.collectPipeLakeData()
        log.info("Done processing SWIMS Data")

        wexWaterQualityDataCollectionService.collectNorthPipeLakeData()
        wexWaterQualityDataCollectionService.collectPipeLakeData()
        wexProfileDataCollectionService.collectNorthPipeLakeData()
        wexProfileDataCollectionService.collectPipeLakeData()

        log.info("Done processing WEx Data")
    }

}
