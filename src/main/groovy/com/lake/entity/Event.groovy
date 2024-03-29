package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

import java.time.LocalDate

@CompileStatic
@Entity
@Table(name = 'event')
class Event extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'value', nullable = false)
    LocalDate value

    @Basic
    @Column(name = 'comment', nullable = true, length = 4000)
    String comment

    @Basic
    @Column(name = 'year', nullable = false)
    Integer year

    @ManyToOne(optional = false)
    @JoinColumn(name = 'site_id', referencedColumnName = 'id', nullable = false)
    Site site

    @ManyToOne(optional = false)
    @JoinColumn(name = 'characteristic_id', referencedColumnName = 'id', nullable = false)
    Characteristic characteristic
}
