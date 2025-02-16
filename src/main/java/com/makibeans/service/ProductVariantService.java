package com.makibeans.service;

import com.makibeans.model.ProductVariant;
import com.makibeans.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantService extends AbstractCrudService<ProductVariant, Long> {

    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public ProductVariantService(JpaRepository<ProductVariant, Long> repository, ProductVariantRepository productVariantRepository) {
        super(repository);
        this.productVariantRepository = productVariantRepository;
    }

    //create
    public ProductVariant createProductVariant(Long productId, Long sizeId, Long PriceInCents, String sku, Long stock)

    //delete

    //update
}
