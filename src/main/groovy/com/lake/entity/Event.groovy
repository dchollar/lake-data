package com.lake.entity

import groovy.transform.CompileStatic

import javax.persistence.*
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
