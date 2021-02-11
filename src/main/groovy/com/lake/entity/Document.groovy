package com.lake.entity

import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.Table
import java.sql.Blob
import java.time.Instant

@Entity
@Table(name = "document")
class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    Site site

    @Basic
    @Column(name = "title", nullable = false, length = 400)
    String title

    @Basic
    @Column(name = "path", nullable = false, length = 1000)
    String path

    @Basic
    @Column(name = "created", nullable = false)
    Instant created

    @Basic
    @Column(name = "last_updated", nullable = false)
    Instant lastUpdated

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "document", nullable = true)
    Blob document

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "text", nullable = true)
    String text
}
