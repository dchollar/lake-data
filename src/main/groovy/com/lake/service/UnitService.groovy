package com.lake.service

import com.lake.dto.UnitDto
import com.lake.entity.Unit
import com.lake.repository.UnitRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
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

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ["unitsBySite", "units", "allUnits"], allEntries = true)
    UnitDto save(UnitDto dto) {
        ConverterUtil.convert(repository.saveAndFlush(ConverterUtil.convert(dto)))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ["unitsBySite", "units", "allUnits"], allEntries = true)
    UnitDto update(Integer id, UnitDto dto) {
        Unit unit = repository.getOne(id)

        unit.unitDescription = dto.unitDescription
        unit.longDescription = dto.longDescription
        unit.shortDescription = dto.shortDescription
        unit.enableDepth = dto.enableDepth
        unit.type = dto.type

        ConverterUtil.convert(repository.saveAndFlush(unit))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ["unitsBySite", "units", "allUnits"], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

}
