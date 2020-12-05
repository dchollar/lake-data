package com.lake.entity

import javax.persistence.*

@Entity
@Table(name = "characteristic_location")
class CharacteristicLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne
    @JoinColumn(name = "characteristic_id", referencedColumnName = "id", nullable = false)
    Characteristic characteristic

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    Location location
}
