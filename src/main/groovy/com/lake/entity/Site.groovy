package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@CompileStatic
@Entity
@Table(name = 'site')
class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'description', nullable = false, length = 100)
    String description

    @Basic
    @Column(name = 'water_body_number', nullable = true, length = 10)
    String waterBodyNumber

    @Basic
    @Column(name = 'dnr_region', nullable = true, length = 10)
    String dnrRegion

    @Basic
    @Column(name = 'geo_region', nullable = true, length = 10)
    String geoRegion

    @OneToMany(mappedBy = 'site')
    Collection<Event> events

    @OneToMany(mappedBy = 'site')
    Collection<Location> locations
}
