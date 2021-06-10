package com.lake.entity

import javax.persistence.*
import java.time.Instant

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
    @Column(name = 'controller', nullable = false, length = 100)
    String controller

    @ManyToOne
    @JoinColumn(name = 'reporter_id', referencedColumnName = 'id', nullable = true)
    Reporter reporter
}
