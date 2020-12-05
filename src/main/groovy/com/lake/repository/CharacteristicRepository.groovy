package com.lake.repository

import com.lake.entity.Characteristic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CharacteristicRepository extends JpaRepository<Characteristic, Integer> {

    @Query(value = 'select * from characteristic where id in ((select characteristic_id from event where site_id = ?) union (select ul.characteristic_id from characteristic_location ul, location l, measurement m where l.site_id = ? and l.id = ul.location_id and ul.id = m.characteristic_location_id))', nativeQuery = true)
    List<Characteristic> findCharacteristicsBySite(Integer siteId, Integer site)
}
