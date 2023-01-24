package com.lake.service

import com.lake.dto.DocumentDto
import com.lake.entity.Document
import com.lake.entity.Site
import com.lake.repository.DocumentRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.sql.rowset.serial.SerialBlob
import java.sql.Blob
import java.time.Instant

@CompileStatic
@Service
class DocumentService {
    @Autowired
    DocumentRepository repository
    @Autowired
    SiteService siteService

    Collection<DocumentDto> findDocumentsContaining(final Integer siteId, final String searchWord, final String category, final String timeZone) {
        String cleanSearch = createRegularExpression(searchWord)
        String cleanCategory = createRegularExpression(category)

        final Collection<Document> documents
        if (cleanSearch && cleanCategory) {
            documents = repository.findBySearchWordAndCategory(siteId, cleanCategory, cleanSearch)
        } else if (cleanSearch) {
            documents = repository.findBySearchWord(siteId, cleanSearch)
        } else if (cleanCategory) {
            documents = repository.findByCategory(siteId, cleanCategory)
        } else {
            documents = repository.findBySite(siteId)
        }
        return ConverterUtil.convertDocuments(documents, timeZone)
    }

    byte[] getDocument(final Integer id) {
        Blob blob = repository.getReferenceById(id).document
        byte[] bytes = blob.getBytes(1, (int) blob.length())
        blob.free()
        return bytes
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    Collection<DocumentDto> getAll(final String timeZone) {
        ConverterUtil.convertDocuments(repository.findAll(), timeZone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @Transactional
    DocumentDto save(final DocumentDto dto, final String timeZone) {
        Document entity = ConverterUtil.convert(dto, new Document())
        entity.site = siteService.getOne(dto.siteId)
        ConverterUtil.convert(repository.saveAndFlush(entity), timeZone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @Transactional
    DocumentDto update(final Integer id, final DocumentDto dto, final String timeZone) {
        Document entity = repository.getReferenceById(id)
        ConverterUtil.convert(dto, entity)
        entity.site = siteService.getOne(dto.siteId)
        ConverterUtil.convert(repository.saveAndFlush(entity), timeZone)
    }

    @Secured(['ROLE_ADMIN', 'ROLE_DOCUMENT_ADMIN'])
    @Transactional
    void delete(final Integer id) {
        repository.deleteById(id)
    }

    @Secured('ROLE_ADMIN')
    @Transactional
    void bulkSave() {
        String subPath = '/home/dan/tmp/PipeLakesRecords'
        File startDir = new File(subPath)
        String[] extensions = ['pdf'].toArray() as String[]
        Collection<File> files = FileUtils.listFiles(startDir, extensions, true)
        files.each { file ->
            byte[] bytes = FileUtils.readFileToByteArray(file)
            Document entity = new Document()
            entity.document = new SerialBlob(bytes)
            entity.fileSize = (bytes.length / 1024).toInteger()
            entity.path = ConverterUtil.cleanPath(StringUtils.replace(StringUtils.replace(file.parent, subPath, ''), '\\', '/'))
            entity.title = StringUtils.replace(file.name, '.pdf', '')
            entity.text = ConverterUtil.convertPdf(bytes)
            entity.created = Instant.now()
            entity.lastUpdated = Instant.now()
            entity.site = siteService.getOne(5)
            repository.save(entity)
        }
    }

    private static String createRegularExpression(final String searchPhrase) {
        String string = ConverterUtil.stripNonAscii(searchPhrase)
        if (string) {
            return string.toUpperCase().replaceAll('\\s+', '|')
        } else {
            return null
        }
    }

}
