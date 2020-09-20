package com.lake.entity

import javax.persistence.*

@Entity
@Table(name = "location")
class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = "description", nullable = false, length = 100)
    String description

    // TODO DO NOT USE ????
    @Deprecated
    @OneToMany(mappedBy = "location")
    Collection<Measurement> measurements

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    Site site

}
