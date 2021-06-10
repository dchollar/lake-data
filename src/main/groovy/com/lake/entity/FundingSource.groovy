package com.lake.entity

import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = 'funding_source')
class FundingSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'name', nullable = false, length = 50)
    String name

    @Basic
    @Column(name = 'description', nullable = false, length = 200)
    String description

    @Basic
    @Column(name = 'start_date', nullable = false)
    LocalDate startDate

    @Basic
    @Column(name = 'end_date', nullable = true)
    LocalDate endDate

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = 'type', nullable = false, length = 15)
    FundingSourceType type


}
