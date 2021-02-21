package com.lake.service

import com.lake.dto.DocumentDto
import com.lake.entity.Document
import com.lake.entity.Site
import com.lake.repository.DocumentRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

import java.sql.Blob
import java.time.Instant

@Service
class DocumentService {
    @Autowired
    DocumentRepository repository
    @Autowired
    SiteService siteService

    Collection<DocumentDto> getDocumentsBySite(Integer siteId, String timeZone) {
        Site site = siteService.getOne(siteId)
        ConverterUtil.convertDocuments(repository.findBySite(site), timeZone)
    }

    byte[] getDocument(Integer id) {
        Blob blob = repository.getOne(id).document
        byte[] bytes = blob.getBytes(1, (int) blob.length())
        blob.free()
        return bytes
    }

    @Secured('ROLE_ADMIN')
    Collection<DocumentDto> getAll(String timeZone) {
        ConverterUtil.convertDocuments(repository.findAll(), timeZone)
    }

    @Secured('ROLE_ADMIN')
    DocumentDto save(DocumentDto dto, String timeZone) {
        Document entity = ConverterUtil.convert(dto, new Document())
        entity.created = Instant.now()
        entity.site = siteService.getOne(dto.siteId)
        ConverterUtil.convert(repository.saveAndFlush(entity), timeZone)
    }

    @Secured('ROLE_ADMIN')
    DocumentDto update(Integer id, DocumentDto dto, String timeZone) {
        Document entity = repository.getOne(id)
        ConverterUtil.convert(dto, entity)
        entity.site = siteService.getOne(dto.siteId)
        ConverterUtil.convert(repository.saveAndFlush(entity), timeZone)
    }

    @Secured('ROLE_ADMIN')
    void delete(Integer id) {
        repository.deleteById(id)
    }
}
