package com.lake.entity

import javax.persistence.*

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

    @OneToMany(mappedBy = 'reporter', cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ReporterRole> roles
}
