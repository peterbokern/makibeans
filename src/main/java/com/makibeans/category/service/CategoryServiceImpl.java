package com.makibeans.category.service;

import com.makibeans.attribute.categoryattribute.service.CategoryAttributeService;
import com.makibeans.category.dto.CategoryRequestDTO;
import com.makibeans.category.dto.CategoryUpdateDTO;
import com.makibeans.category.filter.CategoryAdminFilter;
import com.makibeans.category.filter.CategoryFilter;
import com.makibeans.category.filter.CategoryPublicFilter;
import com.makibeans.category.mapper.CategoryMapper;
import com.makibeans.category.model.Category;
import com.makibeans.category.repository.CategoryRepository;
import com.makibeans.common.util.ImageUtils;
import com.makibeans.common.util.TextUtils;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.web.exceptions.*;
import jakarta.validation.Valid;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repo;
    private final CategoryMapper mapper;
    private final CategoryUsageChecker usageChecker;
    private final CategoryAttributeService categoryAttributeService;
    private final ImageUtils imageUtils;

    @Autowired
    public CategoryServiceImpl(
            CategoryRepository repo,
            CategoryMapper mapper,
            CategoryUsageChecker usageChecker, CategoryAttributeService categoryAttributeService,
            ImageUtils imageUtils
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.usageChecker = usageChecker;
        this.categoryAttributeService = categoryAttributeService;
        this.imageUtils = imageUtils;
    }

    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return repo.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Category getByIdIncludingDeleted(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Category with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoryTree() {
        return repo.findByParentCategoryIsNullAndDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> searchPublic(SearchRequest<CategoryPublicFilter> req) {
        return search(req, CategoryPublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Category> searchAdmin(SearchRequest<CategoryAdminFilter> req) {
        return search(req, CategoryAdminFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> Page<Category> search(SearchRequest<F> req, Class<F> filterClass) {

        Specification<Category> spec =
                SpecificationFactory.fromRequest(req, filterClass);

        Sort sort = new SortResolver(filterClass)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    // -------------------------------------------------------------------------
    // WRITES
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Category create(@Valid CategoryRequestDTO dto) throws BadRequestException {

        String trimmedName = dto.getName().trim();
        String normalizedName = normalize(trimmedName);
        String slug = toSlug(normalizedName);

        Category parent = dto.getParentCategoryId() != null
                ? getById(dto.getParentCategoryId())
                : null;

        if (parent != null) assertNotEqualsParentSlug(parent, slug);

        assertUniqueSlugAmongSiblings(parent, slug, null);

        Category category = new Category();
        category.setName(trimmedName);
        category.setSlug(slug);
        category.setDescription(TextUtils.trim(dto.getDescription()));
        category.setParentCategory(parent);

        return repo.save(category);
    }

    @Override
    @Transactional
    public Category update(Long id, @Valid CategoryUpdateDTO dto) throws BadRequestException {

        // Fetch existing category
        Category category = getById(id);
        String finalSlug = category.getSlug();
        String finalName = category.getName();
        Category finalParent = category.getParentCategory();

        // 1) Parent change
        Long parentId = dto.getParentCategoryId();
        if (parentId != null)  {
            finalParent = getById(parentId);
            validateCircularReference(finalParent, category);
        }

        // 2) Name change
        String newName = dto.getName();
        if (newName != null && !newName.isBlank()) {
            finalName = newName.trim();
            String normalizedNewName = normalize(finalName);
            finalSlug = toSlug(normalizedNewName);
        }

        // Validate slug uniqueness
        boolean changed = nameChanged(category, newName) || parentChanged(category, parentId);

        if (changed) {
            if (finalParent != null) assertNotEqualsParentSlug(finalParent, finalSlug);
            assertUniqueSlugAmongSiblings(finalParent, finalSlug, category.getId());
        }

        // Apply changes
        category.setParentCategory(finalParent);
        category.setName(finalName);
        category.setSlug(finalSlug);

        // 3) Other fields
        mapper.updateEntityFromDTO(dto, category);

        return category;
    }

    private boolean nameChanged(Category category, String newName) {
        if (newName == null || newName.isBlank()) return false;

        String trimmedNewName = newName.trim();
        String normalizedNewName = normalize(trimmedNewName);
        String newSlug = toSlug(normalizedNewName);
        return !newSlug.equalsIgnoreCase(category.getSlug());
    }

    private boolean parentChanged(Category category, Long newParentId) {
        if (newParentId == null) return false;

        Long currentParentId = category.getParentCategory() != null
                ? category.getParentCategory().getId()
                : null;

        return !Objects.equals(currentParentId, newParentId);
    }

    @Override
    @Transactional
    public Category makeRoot(Long categoryId) {
        Category category = getById(categoryId);
        Category currentParent = category.getParentCategory();
        if (currentParent != null) {

            // Ensure slug uniqueness among root categories
            assertUniqueSlugAmongSiblings(null, category.getSlug(), category.getId());

            category.setParentCategory(null);
            currentParent.getSubCategories().removeIf(c -> c.getId().equals(categoryId));

        }
        return category;
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Category category = getById(categoryId);
        assertNotInUse(category);
        categoryAttributeService.deleteByCategory(category);
        category.setDeleted(true);
    }

    @Override
    @Transactional
    public Category restore(Long categoryId) throws BadRequestException {
        Category category = getByIdIncludingDeleted(categoryId);
        assertDeleted(category);
        Category parent = category.getParentCategory();
        String slug = category.getSlug();

        if (parent != null) {
            if (parent.isDeleted()) {
                throw new BadRequestException(
                        String.format("Cannot restore category '%s' because its parent category '%s' is deleted.",
                                category.getName(),
                                parent.getName()));
            }
            assertNotEqualsParentSlug(
                    parent,
                    slug
            );
        }
        assertUniqueSlugAmongSiblings(
                parent,
                slug,
                null
        );
        category.setDeleted(false);
        categoryAttributeService.restoreByCategory(category);
        return category;
    }

    // -------------------------------------------------------------------------
    // IMAGE
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public Category uploadCategoryImage(Long categoryId, MultipartFile image) {
        Category category = getById(categoryId);
        byte[] bytes = imageUtils.validateAndExtractImageBytes(image);
        category.setImage(bytes);
        return category;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getCategoryImage(Long categoryId) {
        Category category = getById(categoryId);
        byte[] img = category.getImage();
        if (img == null) {
            throw new ResourceNotFoundException("Category with ID " + categoryId + " does not have an image.");
        }
        return img;
    }

    @Override
    @Transactional
    public void deleteCategoryImage(Long categoryId) {
        Category category = getById(categoryId);
        category.setImage(null);
    }

    // -------------------------------------------------------------------------
    // VALIDATION
    // -------------------------------------------------------------------------

    void validateCircularReference(Category parentCategory, Category subCategory) {
        Category current = parentCategory;
        while (current != null) {
            if (current.getId() !=null && current.getId().equals(subCategory.getId())) {
                throw new CircularReferenceException(String.format(
                        "Category '%s' cannot be assigned under '%s' because it would create a circular reference.",
                        subCategory.getName(), parentCategory.getName()));
            }
            current = current.getParentCategory();
        }
    }

    private void assertUniqueSlugAmongSiblings(Category parentCategory, String slug, Long excludeId) {

        boolean exists;

        if (parentCategory == null) {
            // root-level uniqueness
            exists = (excludeId == null)
                    ? repo.existsByParentCategoryIsNullAndSlugIgnoreCaseAndDeletedFalse(slug) // create
                    : repo.existsByParentCategoryIsNullAndSlugIgnoreCaseAndIdNotAndDeletedFalse(slug, excludeId); // update
        } else {
            // sibling uniqueness
            exists = (excludeId == null)
                    ? repo.existsByParentCategoryAndSlugIgnoreCaseAndDeletedFalse(parentCategory, slug) // create
                    : repo.existsByParentCategoryAndSlugIgnoreCaseAndIdNotAndDeletedFalse(parentCategory, slug, excludeId); // update
        }

        if (exists) {
            String parentLabel = parentCategory == null ? "ROOT" : parentCategory.getName();
            throw new DuplicateResourceException(
                    String.format("A category with slug '%s' already exists under '%s'.", slug, parentLabel));
        }
    }

    private void assertNotEqualsParentSlug(Category parent, String childSlug) throws BadRequestException {
        if (childSlug == null || childSlug.isBlank()) return;

        if (parent.getSlug() != null && parent.getSlug().equalsIgnoreCase(childSlug)) {
            throw new BadRequestException(String.format(
                    "Category slug '%s' cannot be the same as its parent category slug.", childSlug));
        }
    }

    private void assertNotInUse(Category category) {
        if (usageChecker.isInUse(category)) {
            throw new ResourceInUseException(
                    "Category cannot be deleted because it is in use. " + usageChecker.getUsageDetails(category)
            );
        }
    }

    private void assertDeleted(Category category) throws BadRequestException {
        if (!category.isDeleted()) {
            throw new BadRequestException(
                    "Category with ID " + category.getId() + " is not deleted and cannot be restored."
            );
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private String normalize(String s) {
        return TextUtils.normalizeText(s);
    }

    private String toSlug(String normalized) {
        return TextUtils.toSlug(normalized);
    }
}
