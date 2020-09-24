package com.lake.entity

import javax.persistence.*

@Entity
@Table(name = "unit")
class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = "unit_description", nullable = false, length = 20)
    String unitDescription

    @Basic
    @Column(name = "long_description", nullable = false, length = 100)
    String longDescription

    @Basic
    @Column(name = "short_description", nullable = false, length = 50)
    String shortDescription

    @Basic
    @Column(name = "enable_depth", nullable = false)
    Boolean enableDepth

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = "type", nullable = false, length = 50)
    UnitType type

    @OneToMany(mappedBy = "unit")
    Set<UnitLocation> unitLocations
}
