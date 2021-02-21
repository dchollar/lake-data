package com.lake.entity

import javax.persistence.*
import java.sql.Blob
import java.time.Instant

@Entity
@Table(name = "document")
class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne(fetch = FetchType.LAZY)
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
    @Column(name = "document", nullable = false)
    Blob document

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "text", nullable = false)
    String text
}
