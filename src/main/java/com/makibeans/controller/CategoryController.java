package com.makibeans.controller;

import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping()
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categoriesResponseDTOs = categoryService.getAllCategories();
        return ResponseEntity.ok(categoriesResponseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategory(@Valid @PathVariable Long id) {
        CategoryResponseDTO categoryResponseDTO = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryResponseDTO);
    }

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

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO updatedCategory = categoryService.updateCategory(id, requestDTO);
        return ResponseEntity.ok(updatedCategory);
    }
}
