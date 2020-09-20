package com.lake.entity

import javax.persistence.*

@Entity
@Table(name = "reporter_role")
class ReporterRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Enumerated(EnumType.STRING)
    @Basic
    @Column(name = 'role', nullable = false, length = 50)
    RoleType role

    // TODO DO NOT USE ????
    @Deprecated
    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
    Reporter reporter

}
