package com.lake.service

import com.lake.dto.LocationDto
import com.lake.entity.Location
import com.lake.entity.Site
import com.lake.repository.LocationRepository
import com.lake.repository.SiteRepository
import com.lake.util.ConverterUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Service

@Service
class LocationService {
    @Autowired
    LocationRepository repository
    @Autowired
    SiteRepository siteRepository

    @Secured('ROLE_ADMIN')
    @Cacheable('locations')
    Set<LocationDto> getAll() {
        ConverterUtil.convertLocations(repository.findAll())
    }

    @Cacheable('locationsBySite')
    Set<LocationDto> getLocationsBySite(Integer siteId) {
        Site site = siteRepository.getOne(siteId)
        return ConverterUtil.convertLocations(site.locations)
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locations', 'locationsBySite', 'locationsById'], allEntries = true)
    LocationDto save(LocationDto dto) {
        Location location = ConverterUtil.convert(dto, new Location())
        location.site = siteRepository.getOne(dto.site.id)
        ConverterUtil.convert(repository.saveAndFlush(location))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locations', 'locationsBySite', 'locationsById'], allEntries = true)
    LocationDto update(Integer id, LocationDto dto) {
        Location location = repository.getOne(id)
        ConverterUtil.convert(dto, location)
        ConverterUtil.convert(repository.saveAndFlush(location))
    }

    @Secured('ROLE_ADMIN')
    @CacheEvict(cacheNames = ['locations', 'locationsBySite', 'locationsById'], allEntries = true)
    void delete(Integer id) {
        repository.deleteById(id)
    }

    @Secured(['ROLE_REPORTER', 'ROLE_ADMIN'])
    @CacheEvict(cacheNames = ['locationsBySite', 'locationsById'], allEntries = true)
    void clearCache() {
    }

    @Cacheable('locationsById')
    Location getOne(Integer id) {
        if (id) {
            return repository.getOne(id)
        } else {
            return null
        }
    }

}
