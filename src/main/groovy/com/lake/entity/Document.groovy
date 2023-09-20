package com.lake.entity

import groovy.transform.CompileStatic
import jakarta.persistence.Basic
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.Lob
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

import java.sql.Blob

@CompileStatic
@Entity
@Table(name = 'document')
class Document extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = 'id', nullable = false)
    Integer id

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = 'site_id', referencedColumnName = 'id', nullable = false)
    Site site

    @Basic
    @Column(name = 'title', nullable = false, length = 400)
    String title

    @Basic
    @Column(name = 'path', nullable = false, length = 1000)
    String path

    @Basic
    @Column(name = 'file_size', nullable = false)
    Integer fileSize

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = 'document', nullable = false)
    Blob document

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = 'text', nullable = false)
    String text
}
