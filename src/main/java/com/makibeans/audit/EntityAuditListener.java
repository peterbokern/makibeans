package com.makibeans.audit;

import com.makibeans.model.audit.Auditable;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Instant;

/**
 * Entity listener to automatically set audit fields on entities.
 */

public class EntityAuditListener {

    @PrePersist
    public void OnCreate(Object entity) {
        if (entity instanceof Auditable au) {
            Instant now = Instant.now();
            au.setCreatedAt(now);
            au.setCreatedBy(currentUser());
            au.setUpdatedBy(currentUser());
            au.setUpdatedAt(now);
        }
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        if (entity instanceof Auditable au) {
            Instant now = Instant.now();
            au.setUpdatedBy(currentUser());
            au.setUpdatedAt(now);

            // when soft delete
            if (au.isDeleted() && Boolean.FALSE.equals(au.getWasDeleted())) {
                au.setDeletedAt(now);
                au.setDeletedBy(currentUser());
            }

            // when restore
            if (!au.isDeleted() && Boolean.TRUE.equals(au.getWasDeleted())) {
                au.setDeletedAt(null);
                au.setDeletedBy(null);
            }

            // keep snapshot in sync for subsequent updates in same persistence context
            au.setWasDeleted(au.isDeleted());
        }
    }

    private String currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null) ? auth.getName() : "system";
    }
}
