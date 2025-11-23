package com.makibeans.mapper;

import com.makibeans.dto.audit.AuditableInfo;
import com.makibeans.model.audit.Auditable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
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
