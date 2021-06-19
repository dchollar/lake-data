package com.lake.entity

import groovy.transform.CompileStatic

import javax.persistence.*

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
