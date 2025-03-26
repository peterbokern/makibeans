package com.makibeans.controller;

import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

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
        List<CategoryResponseDTO> categoryResponseDTOs =
                params.containsKey("search") || params.containsKey("sort") || params.containsKey("order")
                        ? categoryService.searchCategories(params)
                        : categoryService.getAllCategories();

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
     * Updates an existing category by its ID.
     *
     * @param id the ID of the category to update
     * @param requestDTO the DTO containing the updated category details
     * @return the updated category as a CategoryResponseDTO
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(updatedCategory);
    }
}
