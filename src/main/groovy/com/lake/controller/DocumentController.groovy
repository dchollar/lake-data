package com.lake.controller

import com.lake.dto.DocumentDto
import com.lake.service.AuditService
import com.lake.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE

@RestController
class DocumentController {

    @Autowired
    DocumentService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/documents/{documentId}/document', produces = APPLICATION_PDF_VALUE)
    @ResponseBody byte[] getDocument(@PathVariable(name = 'documentId', required = true) Integer documentId) {
        auditService.audit(HttpMethod.GET.name(), "/api/documents/${documentId}/document", this.class.simpleName)
        return service.getDocument(documentId)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    Collection<DocumentDto> getAll(@RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.GET.name(), '/api/documents', this.class.simpleName)
        return service.getAll(timezone)
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    DocumentDto create(@RequestBody DocumentDto dto,
                       @RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.POST.name(), '/api/documents', this.class.simpleName)
        return service.save(dto, timezone)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/documents/{documentId}', produces = APPLICATION_JSON_VALUE)
    DocumentDto update(@PathVariable(name = 'documentId', required = true) Integer documentId,
                       @RequestParam(name = 'timezone', required = true) String timezone,
                       @RequestBody DocumentDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/documents/${documentId}", this.class.simpleName)
        return service.update(documentId, dto, timezone)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/reporters/{documentId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'documentId', required = true) Integer documentId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/documents/${documentId}", this.class.simpleName)
        service.delete(documentId)
    }
}
