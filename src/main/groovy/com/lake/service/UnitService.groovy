package com.lake.service

import com.lake.dto.UnitDto
import com.lake.repository.UnitRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UnitService {
    @Autowired
    UnitRepository repository

    Set<UnitDto> getAllUnits() {
        ConverterUtil.convertUnits(repository.findAll())
    }

    UnitDto getById(Integer id) {
        ConverterUtil.convert(repository.getOne(id))
    }

    Set<UnitDto> getUnitsBySite(Integer siteId) {
        ConverterUtil.convertUnits(repository.findUnitsBySite(siteId, siteId))
    }

}
