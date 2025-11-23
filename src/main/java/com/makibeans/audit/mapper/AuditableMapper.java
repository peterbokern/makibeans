package com.makibeans.audit.mapper;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.audit.model.Auditable;

public interface AuditableMapper {

    static AuditableInfo toAuditableInfo(Auditable au) {
        if (au == null) {
            return null;
        }
        return new AuditableInfo(
                au.getCreatedBy(),
                au.getCreatedAt(),
                au.getUpdatedBy(),
                au.getUpdatedAt()
        );
    }
}
