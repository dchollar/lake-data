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
import jakarta.persistence.Table

import java.time.Instant

@CompileStatic
@Entity
@Table(name = 'audit')
class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'created', nullable = false)
    Instant created

    @Basic
    @Column(name = 'http_method', nullable = false, length = 10)
    String httpMethod

    @Basic
    @Column(name = 'endpoint', nullable = false, length = 100)
    String endpoint

    @Basic
    @Column(name = 'controller', nullable = false, length = 45)
    String controller

    @ManyToOne
    @JoinColumn(name = 'reporter_id', referencedColumnName = 'id', nullable = true)
    Reporter reporter
}
