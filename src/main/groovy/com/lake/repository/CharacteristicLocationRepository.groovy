package com.lake.repository

import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicLocation
import com.lake.entity.Location
import com.lake.entity.Site
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@CompileStatic
interface CharacteristicLocationRepository extends JpaRepository<CharacteristicLocation, Integer> {

    CharacteristicLocation findByCharacteristicAndLocation(Characteristic characteristic, Location location)

    List<CharacteristicLocation> findByCharacteristic(Characteristic characteristic)

    List<CharacteristicLocation> findByLocation(Location location)

    @Query('select distinct cl from CharacteristicLocation cl where cl.location.site = ?1')
    List<CharacteristicLocation> findBySite(Site site)

    CharacteristicLocation findByLocationAndCharacteristic(Location location, Characteristic characteristic)
}
