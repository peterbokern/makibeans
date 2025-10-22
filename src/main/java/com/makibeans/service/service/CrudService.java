package com.makibeans.service.service;

import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.audit.Auditable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Objects;

/**
 * Minimal reusable CRUD defaults.
 * - delete(id): soft-delete if entity implements Auditable, otherwise hard-delete
 * - restore(id): only for Auditable entities
 * - getOrThrow(id): helper for read/update flows
 */
public interface CrudService<T, ID> {

    /** Implementors must return their repository. */
    JpaRepository<T, ID> repo();

    /** Readable name for errors/logs. Override if needed. */
    default String entityName() {
        String n = getClass().getSimpleName().replace("ServiceImpl", "").replace("Service", "");
        return n.isBlank() ? "Resource" : n;
    }

    /* ---------- Defaults ---------- */

      /*  default T create(T entity) {
            return repo().save(Objects.requireNonNull(entity, entityName() + " cannot be null."));
        }*/

    /** Typical load-or-404 helper for updates and deletes. */
    default T getOrThrow(ID id) {
        Objects.requireNonNull(id, entityName() + " ID cannot be null.");
        return repo().findById(id).orElseThrow(
                () -> new ResourceNotFoundException(entityName() + " with ID " + id + " not found.")
        );
    }

    /** Soft-delete if possible; otherwise hard-delete. */
    default void delete(ID id) {
        T e = getOrThrow(id);
        if (e instanceof Auditable a) {
            a.setDeleted(true);
            // optionally: a.setDeletedAt(Instant.now());
            repo().save(e);
        } else {
            repo().delete(e);
        }
    }

    /** Explicit soft-delete (throws if entity not Auditable). */
    default void softDelete(ID id) {
        T e = getOrThrow(id);
        if (e instanceof Auditable a) {
            a.setDeleted(true);
            repo().save(e);
        } else {
            throw new IllegalArgumentException(entityName() + " does not support soft deletion.");
        }
    }

    /** Explicit hard delete (use only in admin/maintenance flows). */
    default void hardDelete(ID id) {
        T e = getOrThrow(id);
        repo().delete(e);
    }

    /** Restore a soft-deleted entity (Auditable only). */
    default void restore(ID id) {
        T e = getOrThrow(id);
        if (e instanceof Auditable a) {
            a.setDeleted(false);
            // optionally: a.setDeletedAt(null);
            repo().save(e);
        } else {
            throw new IllegalArgumentException(entityName() + " does not support restoration.");
        }
    }
}