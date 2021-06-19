package com.lake.controller

import com.lake.dto.FundingSourceDto
import com.lake.service.AuditService
import com.lake.service.FundingSourceService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@CompileStatic
@Slf4j
@RestController
class FundingSourceController {

    @Autowired
    FundingSourceService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/fundingSources', produces = APPLICATION_JSON_VALUE)
    Collection<FundingSourceDto> getAll() {
        auditService.audit(HttpMethod.GET.name(), '/api/fundingSources', this.class.simpleName)
        return service.getAll()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/fundingSources', produces = APPLICATION_JSON_VALUE)
    FundingSourceDto create(@RequestBody FundingSourceDto dto) {
        auditService.audit(HttpMethod.POST.name(), '/api/fundingSources', this.class.simpleName)
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/fundingSources/{fundingSourceId}', produces = APPLICATION_JSON_VALUE)
    FundingSourceDto update(@PathVariable(name = 'fundingSourceId', required = true) Integer fundingSourceId,
                            @RequestBody FundingSourceDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/fundingSources/${fundingSourceId}", this.class.simpleName)
        return service.update(fundingSourceId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/fundingSources/{fundingSourceId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'fundingSourceId', required = true) Integer fundingSourceId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/fundingSources/${fundingSourceId}", this.class.simpleName)
        service.delete(fundingSourceId)
    }

}

//PIPE &amp; N PIPE LAKE P&amp;R DISTRICT: Pipe and North Pipe Lake Geochemistry Study	2019	ACTIVE	LAKE_GRANT	Large Scale Lake Planning	02/15/2019	12/31/2021	LPL170719	Activities: lake level monitoring; management plan meetings; watershed delineation and modeling update; groundwater, surface water, and precipitation monitoring using stable isotopes to understand water and nutrient budgets  Project deliverables include:  monitoring data in SWIMS, final monitoring report, and an updated management plan  Specific project conditions:  This scope summarizes the project detail provided in the application and does not negate tasks/deliverables described therein.  The grant sponsor shall submit all data, records, and reports, including GIS-based maps and digital images, to the Department in a format specified by the regional Lakes Biologist.
//PIPE &amp; N PIPE LAKE P&amp;R DISTRICT: Pipe and North Pipe Lake internal Load Study	2019	ACTIVE	LAKE_GRANT	Large Scale Lake Planning	02/15/2019	12/31/2021	LPL170619	Activities: In-lake chemistry monitoring, monitor phosphorus release from lake sediments, alum dosage study, management plan update  Project deliverables include:  Monitoring data in SWIMS database, report summarizing internal phosphorus load, updated management plan  Specific project conditions:  This scope summarizes the project detail provided in the application and does not negate tasks/deliverables described therein.  The grant sponsor shall submit all data, records, and reports, including GIS-based maps and digital images, to the Department in a format specified by the regional Lakes Biologist.
