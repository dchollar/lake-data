package com.lake.service

import com.lake.dto.LocationDto
import com.lake.entity.Location
import com.lake.entity.Site
import com.lake.repository.LocationRepository
import com.lake.repository.SiteRepository
import com.lake.util.ConverterUtil
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@CompileStatic
@Service
class LocationService {
    @Autowired
    LocationRepository repository
    @Autowired
    SiteRepository siteRepository

    @Cacheable('locationDtoById')
    LocationDto getLocation(Integer id) {
        if (id) {
            return ConverterUtil.convert(repository.getById(id))
        } else {
            return null
        }
    }

    @Secured('ROLE_ADMIN')
    @Cacheable('locations')
    Set<LocationDto> getAll() {
        ConverterUtil.convertLocations(repository.findAll())
    }

    Set<LocationDto> getLocationsBySite(Integer siteId) {
        Site site = siteRepository.getById(siteId)
        return ConverterUtil.convertLocations(site.locations)
    }

    Set<LocationDto> getLocationsBySite(Integer siteId, Integer characteristicId, Boolean restricted) {
        Set<LocationDto> dtos = new TreeSet<>()
        Site site = siteRepository.getById(siteId)
        site.locations.each { location ->
            location.characteristicLocations.each {
                if (it.characteristic.id == characteristicId && (!restricted || (restricted && it.measurements.size() > 0))) {
                    dtos.add(ConverterUtil.convert(location))
                }
            }
        }
        return dtos
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locationDtoById', 'locations', 'locationsBySite', 'locationsById'], allEntries = true)
    LocationDto save(LocationDto dto) {
        Location location = ConverterUtil.convert(dto, new Location())
        location.site = siteRepository.getById(dto.siteId)
        ConverterUtil.convert(repository.saveAndFlush(location))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locationDtoById', 'locations', 'locationsBySite', 'locationsById'], allEntries = true)
    LocationDto update(Integer id, LocationDto dto) {
        Location location = repository.getById(id)
        ConverterUtil.convert(dto, location)
        ConverterUtil.convert(repository.saveAndFlush(location))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locationDtoById', 'locations', 'locationsBySite', 'locationsById'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

    @Cacheable('locationsById')
    Location getOne(Integer id) {
        if (id && id > 0) {
            return repository.getById(id)
        } else {
            return null
        }
    }

}
