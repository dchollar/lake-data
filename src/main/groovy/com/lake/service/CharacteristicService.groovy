package com.lake.service

import com.lake.dto.CharacteristicDto
import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicType
import com.lake.repository.CharacteristicRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@CompileStatic
@Service
class CharacteristicService {

    private static final int PIPE_LAKE_SITE_ID = 1
    private static final int NORTH_PIPE_LAKE_SITE_ID = 2

    @Autowired
    CharacteristicRepository repository

    @Cacheable('allCharacteristics')
    Set<CharacteristicDto> getAll() {
        ConverterUtil.convertCharacteristics(repository.findAll())
    }

    @Cacheable('characteristics')
    CharacteristicDto getById(final Integer id) {
        ConverterUtil.convert(repository.getReferenceById(id))
    }

    Set<CharacteristicDto> getCharacteristicsBySite(final Integer siteId) {
        ConverterUtil.convertCharacteristics(repository.findCharacteristicsBySite(siteId, siteId))
    }

    Set<CharacteristicDto> getCharacteristicsBySiteForDataEntry(final Integer siteId) {
        List<Characteristic> characteristics = repository.findAllForDataEntry(siteId)
        if (siteId && (siteId == NORTH_PIPE_LAKE_SITE_ID || siteId == PIPE_LAKE_SITE_ID)) {
            characteristics.addAll(repository.findByType(CharacteristicType.EVENT))
        }
        return ConverterUtil.convertCharacteristics(characteristics)
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    @Transactional
    CharacteristicDto save(final CharacteristicDto dto) {
        ConverterUtil.convert(repository.saveAndFlush(ConverterUtil.convert(dto, new Characteristic())))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    @Transactional
    CharacteristicDto update(final Integer id, final CharacteristicDto dto) {
        Characteristic characteristic = repository.getReferenceById(id)
        ConverterUtil.convert(dto, characteristic)
        ConverterUtil.convert(repository.saveAndFlush(characteristic))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['characteristics', 'allCharacteristics', 'characteristicsById'], allEntries = true)
    @Transactional
    void delete(final Integer id) {
        repository.deleteById(id)
    }

    @CacheEvict(cacheNames = ['characteristicsById'], allEntries = true)
    void clearCache() {
    }

    @Cacheable('characteristicsById')
    Characteristic getOne(final Integer id) {
        if (id) {
            return repository.findById(id).orElse(null)
        } else {
            return null
        }
    }

}
