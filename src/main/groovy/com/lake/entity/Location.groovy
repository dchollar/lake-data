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
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.locationtech.jts.geom.Point

@CompileStatic
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
