package com.lake.service

import com.lake.dto.CharacteristicLocationDto
import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicLocation
import com.lake.entity.Location
import com.lake.entity.Site
import com.lake.repository.CharacteristicLocationRepository
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
class CharacteristicLocationService {

    @Autowired
    CharacteristicLocationRepository repository
    @Autowired
    LocationService locationService
    @Autowired
    CharacteristicService characteristicService

    @Cacheable('characteristicLocation')
    CharacteristicLocation get(final Characteristic characteristic, final Location location) {
        return repository.findByCharacteristicAndLocation(characteristic, location)
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('characteristicLocationBySite')
    List<CharacteristicLocation> getBySite(final Site site) {
        return repository.findBySite(site)
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('characteristicLocationByLocation')
    List<CharacteristicLocation> getByLocation(final Location location) {
        return repository.findByLocation(location)
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('characteristicLocationByCharacteristic')
    List<CharacteristicLocation> getByCharacteristic(final Characteristic characteristic) {
        return repository.findByCharacteristic(characteristic)
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('allCharacteristicLocations')
    Set<CharacteristicLocationDto> getAll() {
        ConverterUtil.convertCharacteristicLocations(repository.findAll())
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['allCharacteristicLocations', 'characteristicLocation', 'characteristicLocationBySite', 'characteristicLocationByLocation', 'characteristicLocationByCharacteristic'], allEntries = true)
    @Transactional
    CharacteristicLocationDto save(final CharacteristicLocationDto dto) {
        CharacteristicLocation entity = new CharacteristicLocation(
                location: locationService.getOne(dto.locationId),
                characteristic: characteristicService.getOne(dto.characteristicId)
        )
        ConverterUtil.convert(repository.saveAndFlush(entity))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['allCharacteristicLocations', 'characteristicLocation', 'characteristicLocationBySite', 'characteristicLocationByLocation', 'characteristicLocationByCharacteristic'], allEntries = true)
    @Transactional
    void delete(final Integer id) {
        repository.deleteById(id)
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['allCharacteristicLocations', 'characteristicLocation', 'characteristicLocationBySite', 'characteristicLocationByLocation', 'characteristicLocationByCharacteristic'], allEntries = true)
    @Transactional
    CharacteristicLocation save(final Characteristic characteristic, final Location location) {
        CharacteristicLocation entity = new CharacteristicLocation(location: location, characteristic: characteristic)
        return repository.saveAndFlush(entity)
    }

}
