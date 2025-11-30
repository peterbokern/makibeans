package com.makibeans.audit.mapper;

import com.makibeans.audit.dto.AuditableInfo;
import com.makibeans.audit.model.Auditable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditableMapper {

    @SuppressWarnings("unused")
    AuditableInfo toAuditInfo(Auditable auditable);

}
