package com.lake.entity

import groovy.transform.CompileStatic

import javax.persistence.*
import java.time.LocalDate

@CompileStatic
@Entity
@Table(name = 'measurement')
class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'collection_date', nullable = false)
    LocalDate collectionDate

    @Basic
    @Column(name = 'value', nullable = false, precision = 3)
    BigDecimal value

    @Basic
    @Column(name = 'depth', nullable = true, precision = 2)
    BigDecimal depth

    @Basic
    @Column(name = 'comment', nullable = true, length = 4000)
    String comment

    @ManyToOne(optional = false)
    @JoinColumn(name = 'characteristic_location_id', referencedColumnName = 'id', nullable = false)
    CharacteristicLocation characteristicLocation

    @ManyToOne(optional = false)
    @JoinColumn(name = 'reporter_id', referencedColumnName = 'id')
    Reporter reporter

    @ManyToOne
    @JoinColumn(name = 'funding_source_id', referencedColumnName = 'id', nullable = true)
    FundingSource fundingSource
}
