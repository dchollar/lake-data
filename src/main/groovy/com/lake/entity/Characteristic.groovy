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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@CompileStatic
@Entity
@Table(name = 'characteristic')
class Characteristic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'unit_description', nullable = false, length = 20)
    String unitDescription

    @Basic
    @Column(name = 'description', nullable = false, length = 100)
    String description

    @Basic
    @Column(name = 'short_description', nullable = false, length = 50)
    String shortDescription

    @Basic
    @Column(name = 'enable_depth', nullable = false)
    Boolean enableDepth

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = 'type', nullable = false, length = 50)
    CharacteristicType type

    @OneToMany(mappedBy = 'characteristic')
    Set<CharacteristicLocation> characteristicLocations
}
