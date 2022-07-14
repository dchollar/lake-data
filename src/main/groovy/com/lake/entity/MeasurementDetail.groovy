package com.lake.entity

import groovy.transform.CompileStatic
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

import javax.persistence.*
import java.time.Instant
import java.time.LocalDate

@CompileStatic
@Entity
@Table(name = 'measurement_detail')
class MeasurementDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'collection_time', nullable = false)
    Instant collectionTime

    @Basic
    @Column(name = 'value', nullable = false, precision = 3)
    BigDecimal value

    @Basic
    @CreatedDate
    @Column(name = 'created', nullable = false, updatable = false)
    Instant created

    @Basic
    @LastModifiedDate
    @Column(name = 'last_updated', nullable = false)
    Instant lastUpdated

    @Basic
    @Column(name = 'source', nullable = true, length = 200)
    String source

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = 'status', nullable = true, length = 25)
    StatusType status

    @ManyToOne(optional = false)
    @JoinColumn(name = 'characteristic_location_id', referencedColumnName = 'id', nullable = false)
    CharacteristicLocation characteristicLocation

}
