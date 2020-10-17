package com.lake.service


import com.lake.dto.SiteDto
import com.lake.repository.SiteRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class SiteService {
    @Autowired
    SiteRepository repository

    @Cacheable("sites")
    Set<SiteDto> getAllSites() {
        ConverterUtil.convertSites(repository.findAll())
    }

}
