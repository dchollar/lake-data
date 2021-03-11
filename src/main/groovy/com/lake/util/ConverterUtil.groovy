package com.lake.util

import com.lake.dto.*
import com.lake.entity.*
import groovy.util.logging.Slf4j
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.LoadLibs
import org.apache.commons.lang3.StringUtils
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper

import javax.sql.rowset.serial.SerialBlob
import java.awt.image.BufferedImage
import java.text.Normalizer
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@Slf4j
class ConverterUtil {

    static Collection<AuditDto> convertAudits(Collection<Audit> entities, String timezone, AuditDto filter) {
        Set<AuditDto> dtos = new TreeSet<>()
        entities.each {
            AuditDto dto = convert(it, timezone)
            if ((!filter.reporterName || (filter.reporterName && dto.reporterName.contains(filter.reporterName)))
                    && (!filter.controller || (filter.controller && dto.controller.contains(filter.controller)))
                    && (!filter.endpoint || (filter.endpoint && dto.endpoint.contains(filter.endpoint)))
                    && (!filter.httpMethod || (filter.httpMethod && dto.httpMethod.contains(filter.httpMethod)))) {
                dtos.add(dto)
            }
        }
        return dtos
    }

    static Collection<DocumentDto> convertDocuments(Collection<Document> entities, String timezone) {
        Set<DocumentDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it, timezone))
        }
        return dtos
    }

    static Collection<MeasurementDto> convertEvents(Collection<Event> entities) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<LocationDto> convertLocations(Collection<Location> entities) {
        Set<LocationDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<MeasurementDto> convertMeasurements(Collection<Measurement> entities) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<ReporterDto> convertReportersForMaintenance(Collection<Reporter> entities) {
        Set<ReporterDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertForMaintenance(it))
        }
        return dtos
    }

    static Collection<SavedMeasurementDto> convertSavedEvents(Collection<Event> entities) {
        Set<SavedMeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertSavedMeasurement(it))
        }
        return dtos
    }

    static Collection<SavedMeasurementDto> convertSavedMeasurements(Collection<Measurement> entities) {
        Set<SavedMeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertSavedMeasurement(it))
        }
        return dtos
    }

    static Set<SiteDto> convertSites(Collection<Site> entities) {
        Set<SiteDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<CharacteristicDto> convertCharacteristics(Collection<Characteristic> entities) {
        Set<CharacteristicDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    //----------------------------------------------------------------------------------

    static AuditDto convert(Audit entity, String timezone) {
        AuditDto dto = new AuditDto()
        dto.id = entity.id
        dto.httpMethod = entity.httpMethod
        dto.endpoint = entity.endpoint
        dto.controller = entity.controller
        if (entity.reporter) {
            dto.reporterName = entity.reporter.firstName + ' ' + entity.reporter.lastName
        } else {
            dto.reporterName = ''
        }
        dto.created = entity.created.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of(timezone)).format('yyyy-MM-dd HH:mm:ss')
        return dto
    }

    static DocumentDto convert(Document entity, String timezone) {
        DocumentDto dto = new DocumentDto()
        dto.id = entity.id
        dto.path = entity.path
        dto.title = entity.title
        dto.siteId = entity.site.id
        dto.lastUpdated = entity.lastUpdated.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of(timezone)).format('yyyy-MM-dd HH:mm')
        dto.created = entity.created.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of(timezone)).format('yyyy-MM-dd HH:mm')
        dto.fileSize = entity.fileSize
        dto.document = null
        return dto
    }

    static MeasurementDto convert(Event entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.value
        dto.dayOfYear = entity.value.dayOfYear
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.characteristic = convert(entity.characteristic)
        dto.reporter = convert(entity.reporter)
        return dto
    }

    static LocationDto convert(Location entity) {
        LocationDto dto = new LocationDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.siteId = entity.site.id
        dto.siteDescription = entity.site.description
        dto.comment = entity.comment
        entity.characteristicLocations.each {
            dto.characteristics.add(convert(it.characteristic))
        }
        return dto
    }

    static MeasurementDto convert(Measurement entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.collectionDate
        dto.dayOfYear = entity.collectionDate.dayOfYear
        dto.value = entity.value
        dto.depth = entity.depth
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.characteristic = convert(entity.characteristicLocation.characteristic)
        dto.location = convert(entity.characteristicLocation.location)
        dto.reporter = convert(entity.reporter)
        return dto
    }

    static ReporterDto convert(Reporter entity) {
        ReporterDto dto = new ReporterDto()
        dto.id = entity.id
        dto.firstName = entity.firstName
        dto.lastName = entity.lastName
        return dto
    }

    static ReporterDto convertForMaintenance(Reporter entity) {
        ReporterDto dto = new ReporterDto()
        dto.id = entity.id
        dto.firstName = entity.firstName
        dto.lastName = entity.lastName
        dto.emailAddress = entity.emailAddress
        dto.username = entity.username
        dto.password = ''
        dto.enabled = entity.enabled
        dto.roleAdmin = entity.roles.any { it.role == RoleType.ROLE_ADMIN }
        dto.rolePowerUser = entity.roles.any { it.role == RoleType.ROLE_POWER_USER }
        dto.roleReporter = entity.roles.any { it.role == RoleType.ROLE_REPORTER }
        return dto
    }

    static SiteDto convert(Site entity) {
        SiteDto dto = new SiteDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.waterBodyNumber = entity.waterBodyNumber
        dto.dnrRegion = entity.dnrRegion
        dto.geoRegion = entity.geoRegion
        return dto
    }

    static CharacteristicDto convert(Characteristic entity) {
        CharacteristicDto dto = new CharacteristicDto()
        dto.id = entity.id
        dto.unitDescription = entity.unitDescription
        dto.description = entity.description
        dto.shortDescription = entity.shortDescription
        dto.enableDepth = entity.enableDepth
        dto.type = entity.type
        return dto
    }

    static SavedMeasurementDto convertSavedMeasurement(Event entity) {
        SavedMeasurementDto dto = new SavedMeasurementDto()
        Characteristic characteristic = entity.characteristic

        dto.id = entity.id
        dto.collectionDate = entity.value
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.value = null
        dto.depth = null
        dto.characteristicId = characteristic.id
        dto.characteristicIdType = characteristic.type
        dto.locationId = null
        dto.siteId = entity.site.id
        return dto
    }

    static SavedMeasurementDto convertSavedMeasurement(Measurement entity) {
        SavedMeasurementDto dto = new SavedMeasurementDto()
        Characteristic characteristic = entity.characteristicLocation.characteristic
        Location location = entity.characteristicLocation.location

        dto.id = entity.id
        dto.collectionDate = entity.collectionDate
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.value = entity.value
        dto.depth = entity.depth
        dto.characteristicId = characteristic.id
        dto.characteristicIdType = characteristic.type
        dto.locationId = location.id
        dto.siteId = location.site.id
        return dto
    }

    //----------------------------------------------------------------------------------

    static Location convert(LocationDto dto, Location entity) {
        entity.id = dto.id
        entity.description = StringUtils.stripToNull(dto.description)
        entity.comment = StringUtils.stripToNull(stripNonAscii(dto.comment))
        return entity
    }

    static Reporter convert(ReporterDto dto, Reporter entity) {
        entity.id = dto.id
        entity.firstName = StringUtils.stripToNull(dto.firstName)
        entity.lastName = StringUtils.stripToNull(dto.lastName)
        entity.emailAddress = StringUtils.stripToNull(dto.emailAddress)
        entity.username = StringUtils.stripToNull(dto.username)
        entity.enabled = dto.enabled

        // only mess with roles for a new reporter
        if (entity.id == null) {
            entity.roles = []
            if (dto.roleReporter) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_REPORTER))
            }
            if (dto.rolePowerUser) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_POWER_USER))
            }
            if (dto.roleAdmin) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_ADMIN))
            }
        }

        return entity
    }

    static Characteristic convert(CharacteristicDto dto, Characteristic entity) {
        entity.id = dto.id
        entity.unitDescription = StringUtils.stripToNull(dto.unitDescription)
        entity.description = StringUtils.stripToNull(dto.description)
        entity.shortDescription = StringUtils.stripToNull(dto.shortDescription)
        entity.enableDepth = dto.enableDepth
        entity.type = dto.type
        return entity
    }

    static Document convert(DocumentDto dto, Document entity) {
        entity.id = dto.id
        entity.path = cleanPath(StringUtils.stripToNull(dto.path))
        entity.title = StringUtils.stripToNull(dto.title)

        entity.document = dto.document == null ? entity.document : new SerialBlob(dto.document.bytes)
        entity.text = dto.document == null ? entity.text : convertPdf(dto.document.bytes)
        entity.fileSize = dto.document == null ? (entity.document.length() / 1024).toInteger() : (dto.document.bytes.length / 1024).toInteger()

        entity.lastUpdated = Instant.now()

        return entity
    }

    static String cleanPath(final String dtoPath) {
        String path = dtoPath.take(1) == '/' ? dtoPath.drop(1) : dtoPath
        String reversePath = path.reverse()
        return reversePath.take(1) == '/' ? reversePath.drop(1).reverse() : path
    }

    static String convertPdf(final byte[] pdf) {
        PDDocument document = PDDocument.load(pdf)
        PDFTextStripper stripper = new PDFTextStripper()
        String allText = stripper.getText(document)

        if (StringUtils.isBlank(allText)) {
            allText = extractTextFromScannedDocument(document)
        }

        String stripped = stripNonAscii(allText)
        stripped = StringUtils.replace(stripped, ' the ', ' ')
        stripped = StringUtils.replace(stripped, ' of ', ' ')
        stripped = StringUtils.replace(stripped, ' a ', ' ')
        stripped = StringUtils.replace(stripped, ' it ', ' ')
        stripped = StringUtils.replace(stripped, '\\s{2,}', ' ')
        stripped = StringUtils.stripToEmpty(stripped)

        document.close()

        return stripped
    }
    
    private static final String TESSERACT_DATA_RESOURCE_NAME = 'tessdata'
    private static final int IMAGE_DPI = 300

    private static String extractTextFromScannedDocument(final PDDocument document) {
        PDFRenderer pdfRenderer = new PDFRenderer(document)

        ITesseract instance = new Tesseract()
        File tessDataFolder = LoadLibs.extractTessResources(TESSERACT_DATA_RESOURCE_NAME)
        instance.setDatapath(tessDataFolder.getPath())

        StringBuilder out = new StringBuilder()
        for (int page = 0; page < document.getNumberOfPages(); page++) {
            BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(page, IMAGE_DPI, ImageType.BINARY)
            out.append(instance.doOCR(bufferedImage))
        }
        return out.toString()
    }

    static String stripNonAscii(final String comment) {
        String s = StringUtils.stripToNull(comment)
        if (s) {
            return Normalizer.normalize(s, Normalizer.Form.NFKD).replaceAll('[^ -~]', '')
        }
        return s
    }

}
