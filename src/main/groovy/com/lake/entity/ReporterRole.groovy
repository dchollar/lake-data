package com.lake.entity

import groovy.transform.CompileStatic

import javax.persistence.*

@CompileStatic
@Entity
@Table(name = 'reporter_role')
class ReporterRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = 'role', nullable = false, length = 50)
    RoleType role

    @ManyToOne(optional = false)
    @JoinColumn(name = 'reporter_id', referencedColumnName = 'id', nullable = false)
    Reporter reporter

}
