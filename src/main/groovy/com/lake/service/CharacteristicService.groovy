package com.lake.service

import com.lake.dto.CharacteristicDto
import com.lake.entity.Characteristic
import com.lake.repository.CharacteristicRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@Service
class CharacteristicService {
    @Autowired
    CharacteristicRepository repository

    @Cacheable('allCharacteristics')
    Set<CharacteristicDto> getAllCharacteristic() {
        ConverterUtil.convertCharacteristics(repository.findAll())
    }

    @Cacheable('characteristics')
    CharacteristicDto getById(Integer id) {
        ConverterUtil.convert(repository.getOne(id))
    }

    @Cacheable('characteristicsBySite')
    Set<CharacteristicDto> getCharacteristicsBySite(Integer siteId) {
        ConverterUtil.convertCharacteristics(repository.findCharacteristicsBySite(siteId, siteId))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristicsBySite', 'characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    CharacteristicDto save(CharacteristicDto dto) {
        ConverterUtil.convert(repository.saveAndFlush(ConverterUtil.convert(dto, new Characteristic())))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristicsBySite', 'characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    CharacteristicDto update(Integer id, CharacteristicDto dto) {
        Characteristic characteristic = repository.getOne(id)
        ConverterUtil.convert(dto, characteristic)
        ConverterUtil.convert(repository.saveAndFlush(characteristic))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristicsBySite', 'characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

    @CacheEvict(cacheNames = ['characteristicsBySite', 'characteristicsById'], allEntries = true)
    void clearCache() {
    }

    @Cacheable('characteristicsById')
    Characteristic getOne(Integer id) {
        if (id) {
            return repository.getOne(id)
        } else {
            return null
        }
    }

}
