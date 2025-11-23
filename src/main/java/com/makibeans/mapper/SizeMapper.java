package com.makibeans.mapper;

import com.makibeans.dto.size.SizeAdminResponseDTO;
import com.makibeans.dto.size.SizePublicResponseDTO;
import com.makibeans.dto.size.SizeResponseDTO;
import com.makibeans.dto.size.SizeUpdateDTO;
import com.makibeans.model.Size;
import com.makibeans.util.MappingUtils;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Size}.
 */
@Mapper(
        componentModel = "spring",
        uses = {MappingUtils.class, AuditableMapper.class}
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
    @Mapping(target = "audit", expression = "java(AuditableMapper.toAuditableInfo(entity))")
    SizeAdminResponseDTO toAdminResponseDTO(Size entity);

    // -------------------------------------------------------------------------
    // Update from DTO
    // -------------------------------------------------------------------------
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(SizeUpdateDTO updateDTO, @MappingTarget Size size);
}
