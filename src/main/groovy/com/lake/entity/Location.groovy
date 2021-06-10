package com.lake.entity

import org.locationtech.jts.geom.Point

import javax.persistence.*

@Entity
@Table(name = 'location')
class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'description', nullable = false, length = 100)
    String description

    @Basic
    @Column(name = 'comment', nullable = true, length = 200)
    String comment

    @Basic
    @Column(name = 'coordinates', nullable = true)
    Point coordinates
    // x is Latitude and y is Longitude
    //44.888325946504935, -93.25492567440092

    @ManyToOne(optional = false)
    @JoinColumn(name = 'site_id', referencedColumnName = 'id', nullable = false)
    Site site

    @OneToMany(mappedBy = 'location')
    Set<CharacteristicLocation> characteristicLocations


}
