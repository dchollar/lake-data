package com.lake.service

import com.lake.dto.UnitDto
import com.lake.repository.UnitRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class UnitService {
    @Autowired
    UnitRepository repository

    @Cacheable("allUnits")
    Set<UnitDto> getAllUnits() {
        ConverterUtil.convertUnits(repository.findAll())
    }

    @Cacheable("units")
    UnitDto getById(Integer id) {
        ConverterUtil.convert(repository.getOne(id))
    }

    @Cacheable("unitsBySite")
    Set<UnitDto> getUnitsBySite(Integer siteId) {
        ConverterUtil.convertUnits(repository.findUnitsBySite(siteId, siteId))
    }

}
