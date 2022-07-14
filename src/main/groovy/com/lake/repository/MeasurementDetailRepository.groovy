package com.lake.repository

import com.lake.entity.CharacteristicLocation
import com.lake.entity.MeasurementDetail
import com.lake.entity.StatusType
import groovy.transform.CompileStatic
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import java.time.LocalDate

@CompileStatic
interface MeasurementDetailRepository extends JpaRepository<MeasurementDetail, Integer> {

    List<MeasurementDetail> findByStatus(StatusType status)

    @Query(value = "select * from measurement_detail d where date(d.collection_time)=?1 and d.characteristic_location_id=?2", nativeQuery = true)
    List<MeasurementDetail>findByDateAndCharacteristicLocation(LocalDate date, Integer id)

}
