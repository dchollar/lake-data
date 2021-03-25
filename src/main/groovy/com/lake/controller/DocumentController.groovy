package com.lake.controller

import com.lake.dto.DocumentDto
import com.lake.service.AuditService
import com.lake.service.DocumentService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import javax.xml.bind.ValidationException

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE

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

    @GetMapping(value = '/public/api/documents/site/{siteId}/search', produces = APPLICATION_JSON_VALUE)
    Collection<DocumentDto> search(@PathVariable(name = 'siteId', required = true) Integer siteId,
                                   @RequestParam(name = 'searchWord', required = true) String searchWord,
                                   @RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.GET.name(), "/public/api/documents/site/${siteId}/search?searchWord=${searchWord}", this.class.simpleName)
        return service.findDocumentsContaining(siteId, searchWord, timezone)
    }

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    Collection<DocumentDto> getAll(@RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.GET.name(), '/api/documents', this.class.simpleName)
        return service.getAll(timezone)
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/documents', produces = APPLICATION_JSON_VALUE)
    DocumentDto create(@ModelAttribute DocumentDto dto, @RequestParam(name = 'timezone', required = true) String timezone) {
        auditService.audit(HttpMethod.POST.name(), '/api/documents', this.class.simpleName)
        if (dto.document == null) {
            throw new ValidationException("a document is required")
        }
        return service.save(dto, timezone)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/documents/{documentId}', produces = APPLICATION_JSON_VALUE)
    DocumentDto update(@PathVariable(name = 'documentId', required = true) Integer documentId,
                       @RequestParam(name = 'timezone', required = true) String timezone,
                       @ModelAttribute DocumentDto dto) {
        auditService.audit(HttpMethod.PUT.name(), "/api/documents/${documentId}", this.class.simpleName)
        dto.id = documentId
        return service.update(documentId, dto, timezone)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/documents/{documentId}', produces = APPLICATION_JSON_VALUE)
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
