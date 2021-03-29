package com.lake.service

import com.lake.dto.SiteDto
import com.lake.entity.Site
import com.lake.repository.SiteRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class SiteService {
    @Autowired
    SiteRepository repository

    @Cacheable('sites')
    Set<SiteDto> getAll() {
        ConverterUtil.convertSites(repository.findAll())
    }

    Set<SiteDto> getSitesWithDocuments() {
        ConverterUtil.convertSites(repository.findAllSitesWithDocuments())
    }

    @Cacheable('siteById')
    Site getOne(Integer id) {
        if (id) {
            return repository.getOne(id)
        } else {
            return null
        }
    }

}
