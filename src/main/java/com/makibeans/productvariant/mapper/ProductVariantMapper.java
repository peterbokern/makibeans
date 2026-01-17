package com.makibeans.productvariant.mapper;

import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
import com.makibeans.productvariant.model.ProductVariant;
import org.mapstruct.*;

/**
 * Mapper for {@link ProductVariant}.
 */
@Mapper(
        componentModel = "spring", uses = {AuditableMapper.class}
)
public interface ProductVariantMapper {

    // -------------------------------------------------------------------------
    // Public DTO
    // -------------------------------------------------------------------------

    @Mapping(source = "isDefault", target = "isDefault")
    ProductVariantPublicResponseDTO toPublicResponseDTO(ProductVariant entity);

    // -------------------------------------------------------------------------
    // Admin DTO (with audit)
    // -------------------------------------------------------------------------

    @Mapping(source = "isDefault", target = "isDefault")
    @Mapping(target = "audit", source = ".")
    ProductVariantAdminResponseDTO toAdminResponseDTO(ProductVariant entity);

    // -------------------------------------------------------------------------
    // Update from DTO
    // -------------------------------------------------------------------------

    @Mapping(target = "isDefault", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProductVariantUpdateDTO dto,
                             @MappingTarget ProductVariant entity);
}
