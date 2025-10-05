
package com.makibeans.model.audit;

import com.makibeans.audit.EntityAuditListener;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Transient;
import java.time.Instant;

@MappedSuperclass
@EntityListeners(EntityAuditListener.class)
@Getter
@Setter
public abstract class Auditable {

    @Transient
    private Boolean wasDeleted;

    @Column(name = "created_by", updatable = false, nullable = true)
    private String createdBy;

    @Column(name = "created_at", updatable = false, nullable = true)
    private Instant createdAt;

    @Column(name = "updated_by", nullable = true)
    private String updatedBy;

    @Column(name = "updated_at", nullable = true)
    private Instant updatedAt;

    //TODO SET DB DEFAULT TO deleted as well
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @Column (name = "deleted_by", nullable = true)
    private String deletedBy;

    @Column (name = "deleted_at", nullable = true)
    private Instant deletedAt;

    // Track if the entity was deleted when loaded from the database
    @PostLoad
    private void captureWasDeleted() {this.wasDeleted = this.deleted;}

    public boolean isDeleted() {
        return this.deleted;
    }
}