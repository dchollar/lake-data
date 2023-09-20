package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

import java.time.LocalDate

@CompileStatic
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
