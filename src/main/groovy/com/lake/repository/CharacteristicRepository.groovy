package com.lake.repository

import com.lake.entity.Characteristic
import com.lake.entity.CharacteristicType
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

@CompileStatic
interface CharacteristicRepository extends JpaRepository<Characteristic, Integer> {

    @Query(value = 'select * from characteristic c where c.id in ((select characteristic_id from event where site_id = ?) union (select ul.characteristic_id from characteristic_location ul, location l, measurement m where l.site_id = ? and l.id = ul.location_id and ul.id = m.characteristic_location_id))', nativeQuery = true)
    List<Characteristic> findCharacteristicsBySite(Integer siteId, Integer site)

    List<Characteristic> findByType(CharacteristicType type)

    @Query(value = 'select c.* from characteristic c join characteristic_location cl on c.id = cl.characteristic_id join location l on cl.location_id = l.id where l.site_id = ?', nativeQuery = true)
    List<Characteristic> findAllForDataEntry(Integer siteId)

}
