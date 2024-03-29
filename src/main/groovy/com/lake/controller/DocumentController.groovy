package com.lake.controller

import com.lake.dto.DocumentDto
import com.lake.service.AuditService
import com.lake.service.DocumentService
import groovy.transform.CompileStatic
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE

@CompileStatic
@RestController
class DocumentController {

    @Autowired
    DocumentService service
    @Autowired
    AuditService auditService

    @GetMapping(value = '/public/api/documents/{documentId}/document', produces = APPLICATION_PDF_VALUE)
    @ResponseBody
    byte[] getDocument(@PathVariable(name = 'documentId', required = true) Integer documentId) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/documents/${documentId}/document", this.class.simpleName)
        return service.getDocument(documentId)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @GetMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    Collection<DocumentDto> getAll(@RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.GET.name(), '/api/documents', this.class.simpleName)
        return service.getAll(timezone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @PostMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    DocumentDto create(@ModelAttribute DocumentDto dto, @RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.POST.name(), '/api/documents', this.class.simpleName)
        if (dto.document == null) {
            throw new ValidationException("a document is required")
        }
        return service.save(dto, timezone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @PutMapping(value = '/api/documents/{documentId}', produces = APPLICATION_JSON_VALUE)
    DocumentDto update(@PathVariable(name = 'documentId', required = true) Integer documentId,
                       @RequestParam(name = 'timezone', required = true) String timezone,
                       @ModelAttribute DocumentDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/documents/${documentId}", this.class.simpleName)
        dto.id = documentId
        return service.update(documentId, dto, timezone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @DeleteMapping(value = '/api/documents/{documentId}')
    void delete(@PathVariable(name = 'documentId', required = true) Integer documentId) {
        auditService.audit(HttpMethod.DELETE.name(), "/api/documents/${documentId}", this.class.simpleName)
        service.delete(documentId)
    }

//    @Secured('ROLE_ADMIN')
//    @GetMapping(value = '/api/documents/bulk', produces = APPLICATION_JSON_VALUE)
//    DocumentDto bulkLoad() {
//        service.bulkSave()
//        return new DocumentDto()
//    }
}
