package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@CompileStatic
@Entity
@Table(name = 'reporter')
class Reporter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = 'first_name', nullable = false, length = 45)
    String firstName

    @Basic
    @Column(name = 'last_name', nullable = false, length = 45)
    String lastName

    @Basic
    @Column(name = 'email_address', nullable = true, length = 200)
    String emailAddress

    @Basic
    @Column(name = 'username', nullable = false, length = 50)
    String username

    @Basic
    @Column(name = 'password', nullable = false, length = 500)
    String password

    @Basic
    @Column(name = 'enabled', nullable = false)
    Boolean enabled

    @Basic
    @Column(name = 'change_password', nullable = false)
    Boolean changePassword

    @OneToMany(mappedBy = 'reporter', cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    Set<ReporterRole> roles
}
