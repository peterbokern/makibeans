package com.makibeans.category.service;

import com.makibeans.attribute.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.attribute.productattribute.repository.ProductAttributeRepository;
import com.makibeans.category.dto.CategoryUsageDTO;
import com.makibeans.category.model.Category;
import com.makibeans.category.repository.CategoryRepository;
import com.makibeans.common.util.TextUtils;
import com.makibeans.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryUsageChecker {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;

    public CategoryUsageChecker(CategoryRepository categoryRepository, CategoryAttributeRepository categoryAttributeRepository, ProductRepository productRepository, ProductAttributeRepository productAttributeRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productAttributeRepository = productAttributeRepository;
    }

    // Summarizes the usage of a category across products, child categories, and product attributes.
    public CategoryUsageDTO summarizeUsage(Category category) {
        boolean usedInProducts = productRepository.existsByCategoryAndDeletedFalse(category);
        boolean usedInChildCategories = categoryRepository.existsByParentCategoryAndDeletedFalse(category);
        boolean usedInProductAttributes = productAttributeRepository.existsByCategoryAttributeCategoryAndDeletedFalse(category);

        return new CategoryUsageDTO(usedInProductAttributes, usedInProducts, usedInChildCategories);
    }

    public boolean isInUse(Category category) {
        CategoryUsageDTO usageSummary = summarizeUsage(category);
        return usageSummary.inUse();
    }

    public String getUsageDetails(Category category) {
        CategoryUsageDTO usageSummary = summarizeUsage(category);

        if (!usageSummary.inUse()) {
            return "Not referenced.";
        }

        List<String> usedIn = new java.util.ArrayList<>();
        if (usageSummary.usedInProductAttributes()) {
            usedIn.add("Product Attributes");
        }

        if (usageSummary.usedInProducts()) {
            usedIn.add("Products");
        }

        if (usageSummary.usedInChildCategories()) {
            usedIn.add("Child Categories");
        }

        return "Referenced by: " + TextUtils.toCommaDelimitedString(usedIn);
    }
}
