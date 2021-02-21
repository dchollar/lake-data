package com.lake.repository

import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicLocation
import com.lake.entity.Location
import org.springframework.data.jpa.repository.JpaRepository

interface CharacteristicLocationRepository extends JpaRepository<CharacteristicLocation, Integer> {

    CharacteristicLocation findByCharacteristicAndLocation(Characteristic characteristic, Location location)

    List<CharacteristicLocation> findByCharacteristic(Characteristic characteristic)

    List<CharacteristicLocation> findByLocation(Location location)

    List<CharacteristicLocation> findByLocationIn(Collection<Location> location)
}
