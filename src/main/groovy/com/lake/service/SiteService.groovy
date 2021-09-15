package com.lake.service

import com.lake.dto.SiteDto
import com.lake.entity.Site
import com.lake.repository.SiteRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@CompileStatic
@Service
class SiteService {
    @Autowired
    SiteRepository repository

    @Cacheable('sites')
    Set<SiteDto> getAll() {
        ConverterUtil.convertSites(repository.findAll())
    }

    @Cacheable('siteById')
    SiteDto get(Integer id) {
        return ConverterUtil.convert(this.getOne(id))
    }

    Set<SiteDto> getSitesWithDocuments() {
        ConverterUtil.convertSites(repository.findAllSitesWithDocuments())
    }

    Site getOne(Integer id) {
        if (id) {
            return repository.getById(id)
        } else {
            return null
        }
    }

}
