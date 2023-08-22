package com.lake.util

import com.lake.dto.*
import com.lake.entity.*
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.util.LoadLibs
import org.apache.commons.lang3.StringUtils
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.WKTReader

import javax.sql.rowset.serial.SerialBlob
import java.awt.image.BufferedImage
import java.text.Normalizer
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

@CompileStatic
@Slf4j
class ConverterUtil {

    private static final DEFAULT_TIMEZONE = 'America/Chicago'

    //----------------------------------------------------------------------------------
    // Collection Converters
    //----------------------------------------------------------------------------------

    static Collection<AuditDto> convertAudits(final Collection<Audit> entities, final String timezone, final AuditDto filter) {
        Set<AuditDto> dtos = new TreeSet<>()
        entities.each {
            AuditDto dto = convert(it, timezone)
            if ((!filter.reporterName || (filter.reporterName && dto.reporterName.containsIgnoreCase(filter.reporterName)))
                    && (!filter.controller || (filter.controller && dto.controller.containsIgnoreCase(filter.controller)))
                    && (!filter.endpoint || (filter.endpoint && dto.endpoint.containsIgnoreCase(filter.endpoint)))
                    && (!filter.httpMethod || (filter.httpMethod && dto.httpMethod.containsIgnoreCase(filter.httpMethod)))) {
                dtos.add(dto)
            }
        }
        return dtos
    }

    static Collection<DocumentDto> convertDocuments(final Collection<Document> entities, String timezone) {
        Set<DocumentDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it, timezone))
        }
        return dtos
    }

    static Collection<MeasurementDto> convertEvents(final Collection<Event> entities) {
        Set<MeasurementDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<FundingSourceDto> convertFundingSources(final Collection<FundingSource> entities) {
        Set<FundingSourceDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<LocationDto> convertLocations(final Collection<Location> entities) {
        Set<LocationDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<CharacteristicLocationDto> convertCharacteristicLocations(final Collection<CharacteristicLocation> entities) {
        Set<CharacteristicLocationDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Collection<MeasurementDto> convertMeasurements(final Collection<Measurement> entities) {
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

    static Collection<MeasurementMaintenanceDto> convertSavedEvents(final Collection<Event> entities) {
        Set<MeasurementMaintenanceDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertSavedMeasurement(it))
        }
        return dtos
    }

    static Collection<MeasurementMaintenanceDto> convertSavedMeasurements(final Collection<Measurement> entities) {
        Set<MeasurementMaintenanceDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convertSavedMeasurement(it))
        }
        return dtos
    }

    static Set<SiteDto> convertSites(final Collection<Site> entities) {
        Set<SiteDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    static Set<CharacteristicDto> convertCharacteristics(final Collection<Characteristic> entities) {
        Set<CharacteristicDto> dtos = new TreeSet<>()
        entities.each {
            dtos.add(convert(it))
        }
        return dtos
    }

    //----------------------------------------------------------------------------------
    // Individual Converters
    //----------------------------------------------------------------------------------

    static String convert(final Instant time, final String timezone) {
        String tz = timezone ?: DEFAULT_TIMEZONE
        return time.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.of(tz)).format('yyyy-MM-dd HH:mm')
    }

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
        dto.created = convert(entity.created, timezone)
        return dto
    }

    static DocumentDto convert(final Document entity, final String timezone) {
        DocumentDto dto = new DocumentDto()
        dto.id = entity.id
        dto.path = entity.path
        dto.title = entity.title
        dto.siteId = entity.site.id
        dto.lastUpdated = convert(entity.lastUpdated, timezone)
        dto.created = convert(entity.created, timezone)
        dto.fileSize = entity.fileSize
        dto.createdByName = "${entity.createdBy.firstName} ${entity.createdBy.lastName}"
        dto.modifiedByName = "${entity.modifiedBy.firstName} ${entity.modifiedBy.lastName}"
        dto.document = null
        return dto
    }

    static MeasurementDto convert(final Event entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.value
        dto.dayOfYear = entity.value.dayOfYear
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.characteristic = convert(entity.characteristic)
        dto.createdBy = convert(entity.createdBy)
        dto.modifiedBy = convert(entity.modifiedBy)
        return dto
    }

    static LocationDto convert(final Location entity) {
        LocationDto dto = new LocationDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.siteId = entity.site.id
        dto.siteDescription = entity.site.description
        dto.comment = entity.comment
        dto.latitude = entity?.coordinates?.x?.toString()
        dto.longitude = entity?.coordinates?.y?.toString()
        return dto
    }

    static CharacteristicLocationDto convert(final CharacteristicLocation entity) {
        CharacteristicLocationDto dto = new CharacteristicLocationDto()
        dto.id = entity.id
        dto.siteId = entity.location.site.id
        dto.locationId = entity.location.id
        dto.characteristicId = entity.characteristic.id
        return dto
    }

    static FundingSourceDto convert(final FundingSource entity) {
        FundingSourceDto dto = new FundingSourceDto()
        dto.id = entity.id
        dto.name = entity.name
        dto.description = entity.description
        dto.startDate = entity.startDate
        dto.endDate = entity.endDate
        dto.type = entity.type
        return dto
    }

    static MeasurementDto convert(final Measurement entity) {
        MeasurementDto dto = new MeasurementDto()
        dto.id = entity.id
        dto.collectionDate = entity.collectionDate
        dto.dayOfYear = entity.collectionDate.dayOfYear
        dto.value = entity.value
        dto.depth = entity.depth
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.characteristic = convert(entity.characteristicLocation.characteristic)
        dto.location = convert(entity.characteristicLocation.location)
        dto.createdBy = convert(entity.createdBy)
        dto.modifiedBy = convert(entity.modifiedBy)
        dto.fundingSource = entity.fundingSource ? convert(entity.fundingSource) : null
        return dto
    }

    static ReporterDto convert(final Reporter entity) {
        ReporterDto dto = new ReporterDto()
        dto.id = entity.id
        dto.firstName = entity.firstName
        dto.lastName = entity.lastName
        return dto
    }

    static ReporterDto convertForMaintenance(final Reporter entity) {
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
        dto.roleDocumentAdmin = entity.roles.any { it.role == RoleType.ROLE_DOCUMENT_ADMIN }
        dto.roleReporter = entity.roles.any { it.role == RoleType.ROLE_REPORTER }
        return dto
    }

    static SiteDto convert(final Site entity) {
        SiteDto dto = new SiteDto()
        dto.id = entity.id
        dto.description = entity.description
        dto.waterBodyNumber = entity.waterBodyNumber
        dto.dnrRegion = entity.dnrRegion
        dto.geoRegion = entity.geoRegion
        return dto
    }

    static CharacteristicDto convert(final Characteristic entity) {
        CharacteristicDto dto = new CharacteristicDto()
        dto.id = entity.id
        dto.unitDescription = entity.unitDescription
        dto.description = entity.description
        dto.shortDescription = entity.shortDescription
        dto.enableDepth = entity.enableDepth
        dto.type = entity.type
        return dto
    }

    static MeasurementMaintenanceDto convertSavedMeasurement(final Event entity) {
        MeasurementMaintenanceDto dto = new MeasurementMaintenanceDto()
        Characteristic characteristic = entity.characteristic

        dto.id = entity.id
        dto.collectionDate = entity.value
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.value = null
        dto.depth = null
        dto.characteristicId = characteristic.id
        dto.characteristicType = characteristic.type
        dto.locationId = null
        dto.siteId = entity.site.id
        dto.createdByName = "${entity.createdBy.firstName} ${entity.createdBy.lastName}"
        dto.modifiedByName = "${entity.modifiedBy.firstName} ${entity.modifiedBy.lastName}"
        return dto
    }

    static MeasurementMaintenanceDto convertSavedMeasurement(final Measurement entity) {
        MeasurementMaintenanceDto dto = new MeasurementMaintenanceDto()
        Characteristic characteristic = entity.characteristicLocation.characteristic
        Location location = entity.characteristicLocation.location

        dto.id = entity.id
        dto.collectionDate = entity.collectionDate
        dto.comment = StringUtils.stripToEmpty(entity.comment)
        dto.value = entity.value
        dto.depth = entity.depth
        dto.characteristicId = characteristic.id
        dto.characteristicType = characteristic.type
        dto.locationId = location.id
        dto.siteId = location.site.id
        dto.createdByName = "${entity.createdBy.firstName} ${entity.createdBy.lastName}"
        dto.modifiedByName = "${entity.modifiedBy.firstName} ${entity.modifiedBy.lastName}"
        dto.fundingSourceId = entity?.fundingSource?.id
        return dto
    }

    //----------------------------------------------------------------------------------
    // Merging Methods
    //----------------------------------------------------------------------------------

    static FundingSource convert(final FundingSourceDto dto, final FundingSource entity) {
        entity.id = dto.id
        entity.name = StringUtils.stripToNull(dto.name)
        entity.description = StringUtils.stripToNull(dto.description)
        entity.startDate = dto.startDate
        entity.endDate = dto.endDate
        entity.type = FundingSourceType.GRANT
        return entity
    }

    static Location convert(final LocationDto dto, final Location entity) {
        entity.id = dto.id
        entity.description = StringUtils.stripToNull(dto.description)
        entity.comment = StringUtils.stripToNull(stripNonAscii(dto.comment))
        entity.coordinates = createPoint(dto)
        return entity
    }

    static Reporter convert(final ReporterDto dto, final Reporter entity) {
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
            if (dto.roleDocumentAdmin) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_DOCUMENT_ADMIN))
            }
            if (dto.roleAdmin) {
                entity.roles.add(new ReporterRole(reporter: entity, role: RoleType.ROLE_ADMIN))
            }
        }

        return entity
    }

    static Characteristic convert(final CharacteristicDto dto, final Characteristic entity) {
        entity.id = dto.id
        entity.unitDescription = StringUtils.stripToNull(dto.unitDescription)
        entity.description = StringUtils.stripToNull(dto.description)
        entity.shortDescription = StringUtils.stripToNull(dto.shortDescription)
        entity.enableDepth = dto.enableDepth
        entity.type = dto.type
        return entity
    }

    static Document convert(final DocumentDto dto, final Document entity) {
        entity.id = dto.id
        entity.path = cleanPath(StringUtils.stripToNull(dto.path))
        entity.title = StringUtils.stripToNull(dto.title)

        // if a new document is not being uploaded then document will be null.
        entity.document = dto.document == null ? entity.document : new SerialBlob(dto.document.bytes)
        entity.text = dto.document == null ? entity.text : convertPdf(dto.document.bytes)
        entity.fileSize = dto.document == null ? entity.fileSize : (dto.document.bytes.length / 1024).toInteger()

        return entity
    }

    //----------------------------------------------------------------------------------
    // Helping Methods
    //----------------------------------------------------------------------------------

    private static Point createPoint(final LocationDto dto) {
        String lat = StringUtils.stripToNull(dto.latitude)
        String lon = StringUtils.stripToNull(dto.longitude)
        if (lat && lon) {
            String pointString = "POINT (${lat} ${lon})"
            return new WKTReader().read(pointString) as Point
        } else {
            return null
        }
    }

    static String cleanPath(final String dtoPath) {
        String path = dtoPath.take(1) == '/' ? dtoPath.drop(1) : dtoPath
        String reversePath = path.reverse()
        return reversePath.take(1) == '/' ? reversePath.drop(1).reverse() : path
    }

    static String convertPdf(final byte[] pdf) {
        PDDocument document = Loader.loadPDF(pdf)
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
        stripped = StringUtils.stripToNull(stripped)
        stripped = StringUtils.upperCase(stripped)

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
