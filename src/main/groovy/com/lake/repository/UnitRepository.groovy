package com.lake.repository

import com.lake.entity.Unit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UnitRepository extends JpaRepository<Unit, Integer> {

    @Query(value = 'select * from unit where id in ((select unit_id from event where site_id = ?) union (select ul.unit_id from unit_location ul, location l, measurement m where l.site_id = ? and l.id = ul.location_id and ul.id = m.unit_location_id))', nativeQuery = true)
    List<Unit> findUnitsBySite(Integer siteId, Integer site)
}
