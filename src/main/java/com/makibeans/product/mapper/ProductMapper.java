package com.makibeans.product.mapper;

import com.makibeans.product.dto.PriceRangeDTO;
import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.repository.PriceRange;
import com.makibeans.productattribute.mapper.ProductAttributeMapper;
import com.makibeans.audit.mapper.AuditableMapper;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.model.Product;
import org.mapstruct.*;

import java.util.Map;

@Mapper(componentModel = "spring", uses = {ProductVariantMapper.class, ProductAttributeMapper.class, AuditableMapper.class})
public interface ProductMapper {

    // --- Public DTO mapping ---
    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(source = "productVariants", target="variants")
    @Mapping(source = "productAttributes", target="attributes")
    ProductPublicResponseDTO toPublicResponseDTO(
            Product entity,
            @Context Map<Long, PriceRange> priceRanges
    );

    @AfterMapping
    default void setPriceRangePublic(
            Product product,
            @MappingTarget ProductPublicResponseDTO dto,
            @Context Map<Long, PriceRange> priceRanges
    ) {
        PriceRange pr = priceRanges.get(product.getId());

        if (pr == null)  {
            dto.setPrice(null);
            return;
        }

        dto.setPrice(new PriceRangeDTO(
                pr.getMinPriceInCents(),
                pr.getMaxPriceInCents())
        );
    }

    // --- Admin DTO mapping with audit ---

    @Mapping(source = ".", target = "imageUrl", qualifiedByName = "getImageUrl")
    @Mapping(target = "audit", source = ".")
    @Mapping(source = "productVariants", target="variants")
    @Mapping(source = "productAttributes", target="attributes")
    ProductAdminResponseDTO toAdminResponseDTO(
            Product entity,
            @Context Map<Long, PriceRange> priceRanges);

    @AfterMapping
    default void setPriceRangeAdmin(
            Product product,
            @MappingTarget ProductAdminResponseDTO dto,
            @Context Map<Long, PriceRange> priceRanges
    ) {
        PriceRange pr = priceRanges.get(product.getId());

        if (pr == null)  {
            dto.setPrice(null);
            return;
        }

        dto.setPrice(new PriceRangeDTO(
                pr.getMinPriceInCents(),
                pr.getMaxPriceInCents())
        );
    }

    @Named("getImageUrl")
    default String getImageUrl(Product product) {
        return product.getImage() != null
                ? "/products/" + product.getId() + "/image"
                : null;
    }

    @Named("trimDescription")
    default String trimDescription(String description) {
        return description != null ? description.trim() : null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", qualifiedByName = "trimDescription")
    void updateEntityFromDTO(ProductUpdateDTO updateDTO, @MappingTarget Product product);
}
