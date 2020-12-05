package com.lake.entity

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "event")
class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = "value", nullable = false)
    LocalDate value

    @Basic
    @Column(name = "comment", nullable = true, length = 4000)
    String comment

    @Basic
    @Column(name = 'year', nullable = false)
    Integer year

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    Site site

    @ManyToOne
    @JoinColumn(name = "characteristic_id", referencedColumnName = "id", nullable = false)
    Characteristic characteristic

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    Reporter reporter
}
