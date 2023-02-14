package com.lake.service

import com.lake.dto.DocumentDto
import com.lake.entity.Document
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
    private static final Integer DEFAULT_SITE_ID = 5 //Pipe & North Pipe Lakes

    @Autowired
    DocumentRepository repository
    @Autowired
    SiteService siteService

    Collection<DocumentDto> findDocuments(final Integer siteId, final String searchWord, final String category, final String timeZone) {
        Collection<Document> documents = search(siteId, searchWord, category)
        return ConverterUtil.convertDocuments(documents, timeZone)
    }

    byte[] getLatestDocument(final String category) {
        final Collection<Document> documents = search(DEFAULT_SITE_ID, null, category)
        Document doc = documents.sort { a, b -> a.created <=> b.created }.first()
        return getBytes(doc)
    }

    byte[] getDocument(final Integer id) {
        return getBytes(repository.getReferenceById(id))
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
            Document entity = createDocument(file, subPath)
            repository.save(entity)
        }
    }

    private Document createDocument(final File file, final String subPath) {
        Instant now = Instant.now()
        byte[] bytes = FileUtils.readFileToByteArray(file)
        Document entity = new Document()
        entity.document = new SerialBlob(bytes)
        entity.fileSize = (bytes.length / 1024).toInteger()
        entity.path = ConverterUtil.cleanPath(StringUtils.replace(StringUtils.replace(file.parent, subPath, ''), '\\', '/'))
        entity.title = StringUtils.replace(file.name, '.pdf', '')
        entity.text = ConverterUtil.convertPdf(bytes)
        entity.created = now
        entity.lastUpdated = now
        entity.site = siteService.getOne(DEFAULT_SITE_ID)
        return entity
    }

    private Collection<Document> search(final int siteId, final String searchWord, final String category) {
        String cleanSearch = createRegularExpression(searchWord)
        String cleanCategory = createRegularExpression(category)
        String cleanTitle = cleanCategory

        final Collection<Document> documents
        if (cleanSearch && cleanCategory) {
            documents = repository.findBySearchWordAndCategory(siteId, cleanCategory, cleanTitle, cleanSearch)
        } else if (cleanSearch) {
            documents = repository.findBySearchWord(siteId, cleanSearch)
        } else if (cleanCategory) {
            documents = repository.findByCategory(siteId, cleanCategory, cleanTitle)
        } else {
            documents = repository.findBySite(siteId)
        }
        return documents
    }

    private static byte[] getBytes(Document document) {
        if (document && document.document) {
            Blob blob = document.document
            byte[] bytes = blob.getBytes(1, (int) blob.length())
            blob.free()
            return bytes
        } else {
            return new byte[0]
        }
    }

    private static String createRegularExpression(final String searchPhrase) {
        //TODO figure something out where we can do a phrase and individual words
        String string = ConverterUtil.stripNonAscii(searchPhrase)
        if (string) {
            return string.toUpperCase().replaceAll('\\s+', ' ')
        } else {
            return null
        }
    }

}
