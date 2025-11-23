package com.makibeans.controller;

import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryResponseDTO;
import com.makibeans.dto.category.CategoryUpdateDTO;
import com.makibeans.exceptions.ImageProcessingException;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.CategoryFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.makibeans.util.FileTypeUtils.detectImageContentType;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "CRUD operations and image handling for product categories")
public class CategoryController {

    private final CategoryService service;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    /**
     * GET by id (simple read).
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * GET: paginated list with query params bound into the same Filter DTO used by POST.
     * Example: /api/category-attributes?categoryId=1,2&attributeName=origin&sort=id,desc&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDTO>> getAll(
            @Valid @ModelAttribute CategoryFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    /**
     * POST: JSON search with same SearchRequest used by GET.
     */
    @PostMapping("/search")
    public ResponseEntity<Page<CategoryResponseDTO>> search(
            @Valid @RequestBody SearchRequest<CategoryFilter> req,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<CategoryFilter> merged = SearchRequestUtils.mergeWithPageable(req, pageable);
        if (merged.getIncludeDeleted() == null) merged.setIncludeDeleted(false);
        return ResponseEntity.ok(service.search(merged));
    }


    /**
     * Retrieves the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be retrieved.
     * @return a ResponseEntity containing the byte array representing the category image.
     */
    @Operation(summary = "Get image of category by ID")
    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable("id") Long categoryId) {
        byte[] image = service.getCategoryImage(categoryId);
        return ResponseEntity
                .ok()
                .header("Content-Type", detectImageContentType(image))
                .body(image);
    }

    /**
     * Creates a new category.
     *
     * @param requestDTO the DTO containing the category details
     * @return a ResponseEntity containing the newly created CategoryResponseDTO
     */
    @Operation(summary = "Create a new category")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO responseDTO = service.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Uploads or updates the image of a category.
     *
     * @param categoryId the ID of the category.
     * @param image      the MultipartFile representing the image.
     * @return the updated CategoryResponseDTO.
     * @throws ImageProcessingException if validation or reading fails.
     */
    @Operation(summary = "Upload or update category image")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/image")
    public ResponseEntity<CategoryResponseDTO> uploadCategoryImage(
            @PathVariable("id") Long categoryId,
            @RequestParam("image") MultipartFile image) {

        String originalFilename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "unknown";
        String fileType = image.getContentType() != null ? image.getContentType() : "application/octet-stream";

        CategoryResponseDTO categoryResponseDTO = service.uploadCategoryImage(categoryId, image);

        logger.info("Uploaded image for category (id: {}, name: {}, filename: {}, type: {})", categoryId, categoryResponseDTO.getName(), originalFilename, fileType);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Upload-Message", "Category image uploaded successfully for '" + categoryResponseDTO.getName() + "'");
        headers.add("X-Original-Filename", originalFilename);
        headers.add("X-File-Type", fileType);

        return ResponseEntity.ok()
                .headers(headers)
                .body(categoryResponseDTO);
    }

    /**
     * Updates an existing category by its ID.
     *
     * @param id the ID of the category to update
     * @param updateDTO the DTO containing the updated category details
     * @return the updated category as a CategoryResponseDTO
     */
    @Operation(summary = "Update existing category")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        CategoryResponseDTO updatedCategory = service.update(id, updateDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Deletes a Category by its unique identifier.
     *
     * @param id the unique identifier of the Category to delete
     * @return a ResponseEntity with no content if the deletion was successful
     */
    @Operation(summary = "Delete category by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be deleted.
     * @return a ResponseEntity indicating the result of the operation.
     */
    @Operation(summary = "Delete category image by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteCategoryImage(@PathVariable("id") Long categoryId) {
        service.deleteCategoryImage(categoryId);
        return ResponseEntity.noContent().build();
    }
}
