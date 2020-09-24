package com.lake.entity

import javax.persistence.*

@Entity
@Table(name = "unit_location")
class UnitLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne
    @JoinColumn(name = "unit_id", referencedColumnName = "id", nullable = false)
    Unit unit

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    Location location
}
