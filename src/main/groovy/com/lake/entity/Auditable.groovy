package com.lake.entity

import groovy.transform.CompileStatic
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

import jakarta.persistence.*
import java.time.Instant

@CompileStatic
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
abstract class Auditable {

    @Basic
    @CreatedDate
    @Column(name = 'created', nullable = false, updatable = false)
    Instant created

    @Basic
    @LastModifiedDate
    @Column(name = 'last_updated', nullable = false)
    Instant lastUpdated

    @ManyToOne(optional = false)
    @JoinColumn(name = 'created_by', referencedColumnName = 'id', nullable = false, updatable = false)
    @CreatedBy
    Reporter createdBy

    @ManyToOne(optional = false)
    @JoinColumn(name = 'modified_by', referencedColumnName = 'id', nullable = false)
    @LastModifiedBy
    Reporter modifiedBy

}
