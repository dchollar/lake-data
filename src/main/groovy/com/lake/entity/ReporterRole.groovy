package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

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
