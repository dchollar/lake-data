package com.lake.entity

import javax.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "measurement")
class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @Basic
    @Column(name = "collection_date", nullable = false)
    LocalDate collectionDate

    @Basic
    @Column(name = "value", nullable = false, precision = 3)
    BigDecimal value

    @Basic
    @Column(name = "depth", nullable = true, precision = 2)
    BigDecimal depth

    @Basic
    @Column(name = "comment", nullable = true, length = 4000)
    String comment

    @ManyToOne
    @JoinColumn(name = "unit_location_id", referencedColumnName = "id", nullable = false)
    UnitLocation unitLocation

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id")
    Reporter reporter
}
