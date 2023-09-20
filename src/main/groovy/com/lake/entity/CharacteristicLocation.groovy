package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@CompileStatic
@Entity
@Table(name = 'characteristic_location')
class CharacteristicLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne(optional = false)
    @JoinColumn(name = 'characteristic_id', referencedColumnName = 'id', nullable = false)
    Characteristic characteristic

    @ManyToOne(optional = false)
    @JoinColumn(name = 'location_id', referencedColumnName = 'id', nullable = false)
    Location location

    @OneToMany(mappedBy = 'characteristicLocation')
    Set<Measurement> measurements
}
