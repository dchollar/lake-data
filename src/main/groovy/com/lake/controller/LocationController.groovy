package com.lake.controller

import com.lake.dto.LocationDto
import com.lake.service.LocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@RestController
class LocationController {

    @Autowired
    LocationService service

    @Secured('ROLE_ADMIN')
    @GetMapping(value = '/api/locations', produces = APPLICATION_JSON_VALUE)
    Collection<LocationDto> getAll() {
        return service.getAll()
    }

    @Secured('ROLE_ADMIN')
    @PostMapping(value = '/api/locations', produces = APPLICATION_JSON_VALUE)
    LocationDto create(@RequestBody LocationDto dto) {
        return service.save(dto)
    }

    @Secured('ROLE_ADMIN')
    @PutMapping(value = '/api/locations/{locationId}', produces = APPLICATION_JSON_VALUE)
    LocationDto update(@PathVariable(name = 'locationId', required = true) Integer locationId,
                       @RequestBody LocationDto dto) {
        return service.update(locationId, dto)
    }

    @Secured('ROLE_ADMIN')
    @DeleteMapping(value = '/api/locations/{locationId}', produces = APPLICATION_JSON_VALUE)
    void delete(@PathVariable(name = 'locationId', required = true) Integer locationId) {
        service.delete(locationId)
    }


}
