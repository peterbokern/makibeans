package com.makibeans.controller;

import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.dto.CategoryUpdateDTO;
import com.makibeans.exceptions.ImageProcessingException;
import com.makibeans.service.CategoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;


import static com.makibeans.util.FileTypeUtils.detectImageContentType;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final Logger logger = LoggerFactory.getLogger(CategoryController.class);


    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Retrieves a list of categories based on the provided search and sort parameters.
     * If no search or sort parameters are provided, all categories are retrieved.
     *
     * @param params a map of search and sort parameters
     * @return a ResponseEntity containing a list of CategoryResponseDTOs
     */

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(@RequestParam Map<String, String> params) {
        List<CategoryResponseDTO> categoryResponseDTOs = categoryService.findBySearchQuery(params);

        return ResponseEntity.ok(categoryResponseDTOs);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return a ResponseEntity containing the CategoryResponseDTO representing the category
     */

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@Valid @PathVariable Long id) {
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponseDTO);
    }

    /**
     * Retrieves the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be retrieved.
     * @return a ResponseEntity containing the byte array representing the category image.
     */

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getCategoryImage(@PathVariable("id") Long categoryId) {
        byte[] image = categoryService.getCategoryImage(categoryId);
        return ResponseEntity
                .ok()
                .header("Content-Type", detectImageContentType(image)) // optionally dynamic if needed
                .body(image);
    }

    /**
     * Creates a new category.
     *
     * @param requestDTO the DTO containing the category details
     * @return a ResponseEntity containing the newly created CategoryResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping()
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO responseDTO =categoryService.createCategory(requestDTO);
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/image")
    public ResponseEntity<CategoryResponseDTO> uploadCategoryImage(
            @PathVariable("id") Long categoryId,
            @RequestParam("image") MultipartFile image) {

        String originalFilename = image.getOriginalFilename() != null ? image.getOriginalFilename() : "unknown";
        String fileType = image.getContentType() != null ? image.getContentType() : "application/octet-stream";

        CategoryResponseDTO categoryResponseDTO = categoryService.uploadCategoryImage(categoryId, image);

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

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO updateDTO) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, updateDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    /**
     * Deletes a Category by its unique identifier.
     *
     * @param id the unique identifier of the Category to delete
     * @return a ResponseEntity with no content if the deletion was successful
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes the image of a category by its ID.
     *
     * @param categoryId the ID of the category whose image is to be deleted.
     * @return a ResponseEntity indicating the result of the operation.
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteCategoryImage(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategoryImage(categoryId);
        return ResponseEntity.ok().build();
    }
}
