/**
 * Abstract base class for auditable entities.
 * Provides fields for tracking creation and modification metadata.
 *
 * Fields:
 * - `createdAt`: Timestamp of when the entity was created.
 * - `updatedAt`: Timestamp of when the entity was last updated.
 * - `createdBy`: Identifier of the user who created the entity.
 * - `updatedBy`: Identifier of the user who last modified the entity.
 *
 * Annotations:
 * - `@MappedSuperclass`: Indicates this class is a base class for JPA entities.
 * - `@EntityListeners(AuditingEntityListener.class)`: Enables auditing functionality.
 * - `@CreatedDate`, `@LastModifiedDate`: Automatically populate timestamps.
 * - `@CreatedBy`, `@LastModifiedBy`: Automatically populate user identifiers.
 */

package com.makibeans.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

// Mapped superclass to be extended by other entities
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable {

    @Column(name = "created_by", updatable = false, nullable = true)
    @CreatedBy
    private String createdBy;

    @Column(name = "created_at", updatable = false, nullable = true)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "updated_by", nullable = true)
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "updated_at", nullable = true)
    @LastModifiedDate
    private Instant updatedAt;
}