package com.makibeans.size.mapper;

import com.makibeans.size.dto.SizeAdminResponseDTO;
import com.makibeans.size.dto.SizePublicResponseDTO;
import com.makibeans.size.dto.SizeResponseDTO;
import com.makibeans.size.dto.SizeUpdateDTO;
import com.makibeans.size.model.Size;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Size}.
 */
@Mapper(
        componentModel = "spring"
)
public interface SizeMapper {

    // -------------------------------------------------------------------------
    // Legacy/general DTO – keep temporarily if still used anywhere
    // -------------------------------------------------------------------------
    SizeResponseDTO toResponseDTO(Size entity);

    // -------------------------------------------------------------------------
    // Public DTO mapping
    // -------------------------------------------------------------------------
    SizePublicResponseDTO toPublicResponseDTO(Size entity);

    // -------------------------------------------------------------------------
    // Admin DTO mapping (with audit)
    // -------------------------------------------------------------------------
    @Mapping(target = "audit", expression = "java(com.makibeans.audit.mapper.AuditableMapper.toAuditableInfo(entity))")
    SizeAdminResponseDTO toAdminResponseDTO(Size entity);

    // -------------------------------------------------------------------------
    // Update from DTO
    // -------------------------------------------------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(SizeUpdateDTO updateDTO, @MappingTarget Size size);
}
