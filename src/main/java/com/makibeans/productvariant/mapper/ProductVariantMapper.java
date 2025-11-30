package com.makibeans.productvariant.mapper;

import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;
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
    // Legacy/general DTO (keep temporarily)
    // -------------------------------------------------------------------------
    ProductVariantResponseDTO toResponseDTO(ProductVariant entity);

    // -------------------------------------------------------------------------
    // Public DTO
    // -------------------------------------------------------------------------

    @Mapping(source = "product.id",        target = "productId")
    @Mapping(source = "product.name",      target = "productName")
    @Mapping(source = "size.id",           target = "sizeId")
    @Mapping(source = "size.name",         target = "sizeName")
    ProductVariantPublicResponseDTO toPublicResponseDTO(ProductVariant entity);

    // -------------------------------------------------------------------------
    // Admin DTO (with audit)
    // -------------------------------------------------------------------------

    @Mapping(source = "product.id",        target = "productId")
    @Mapping(source = "product.name",      target = "productName")
    @Mapping(source = "size.id",           target = "sizeId")
    @Mapping(source = "size.name",         target = "sizeName")
    @Mapping(target = "audit", source = ".")
    ProductVariantAdminResponseDTO toAdminResponseDTO(ProductVariant entity);

    // -------------------------------------------------------------------------
    // Update from DTO
    // -------------------------------------------------------------------------

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(ProductVariantUpdateDTO dto,
                             @MappingTarget ProductVariant entity);
}
