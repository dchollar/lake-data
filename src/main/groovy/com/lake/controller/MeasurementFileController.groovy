package com.lake.controller

import com.lake.entity.Location
import com.lake.service.AuditService
import com.lake.service.LocationService
import com.lake.service.MeasurementFileService
import com.lake.service.SiteService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.http.HttpMethod
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@CompileStatic
@Slf4j
@Controller
class MeasurementFileController {

    @Autowired
    LocationService locationService
    @Autowired
    AuditService auditService
    @Autowired
    SiteService siteService
    @Autowired
    MeasurementFileService measurementFileService
    @Autowired
    BuildProperties buildProperties

    @Secured(['ROLE_ADMIN'])
    @GetMapping('/dataFileUpload')
    String dataFileUpload(Model model) {
        auditService.audit(HttpMethod.GET.name(), '/dataFileUpload', this.class.simpleName)
        model.addAttribute('sites', siteService.getAll())
        model.addAttribute('version', buildProperties.getVersion())
        return 'dataFileUpload'
    }

    @Secured(['ROLE_ADMIN'])
    @PostMapping("/measurementFileUpload")
    String submit(
            @RequestParam MultipartFile dataFile,
            @RequestParam Integer locationsChoice,
            Model model) {

        auditService.audit(HttpMethod.GET.name(), '/dataFileUpload', this.class.simpleName)

        try {
            model.addAttribute('errorMessage', '')
            model.addAttribute('sites', siteService.getAll())
            model.addAttribute('version', buildProperties.getVersion())

            Location location = locationService.getOne(locationsChoice)
            measurementFileService.saveDataFile(dataFile, location)
        } catch (Exception e) {
            model.addAttribute('errorMessage', ExceptionUtils.getRootCauseMessage(e))
        }

        return "dataFileUpload"
    }

}
