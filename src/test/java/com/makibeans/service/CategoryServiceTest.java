/*
package com.makibeans.service;

import com.makibeans.dto.CategoryRequestDTO;
import com.makibeans.dto.CategoryResponseDTO;
import com.makibeans.exceptions.CircularReferenceException;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void whenCreatingValidRootCategory_thenReturnsCategory() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Coffee", "Description", "imageUrl", null);
        Category category = new Category("Coffee", "Description", "imageUrl");
        CategoryResponseDTO responseDTO =
                new CategoryResponseDTO(1L, "Coffee", "Description", "imageUrl", null, Collections.emptyList(), Collections.emptyList());

        when(categoryMapper.toEntity(any())).thenReturn(category);
        when(categoryRepository.existsByNameAndParentCategory("Coffee", null)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Coffee", result.getName());
        assertNull(result.getParentCategoryId());

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenCreatingValidSubCategory_thenReturnsCategory() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Beans", "Subcategory", "imageUrl", 1L);
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category subCategory = new Category("Beans", "Subcategory", "imageUrl", parentCategory);
        CategoryResponseDTO responseDTO =
                new CategoryResponseDTO(2L, "Beans", "Subcategory", "imageUrl", 1L, Collections.emptyList(), Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryMapper.toEntity(any())).thenReturn(subCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(subCategory);
        when(categoryMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        assertNotNull(result);
        assertEquals("Beans", result.getName());
        assertEquals(1L, result.getParentCategoryId());

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenCreatingSubCategoryWithNonExistentParent_thenThrowsException() {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("NonExistent", "Description", "imageUrl", 999L);
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.createCategory(requestDTO),
                "Expected ResourceNotFoundException when parent category ID does not exist.");

        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void whenValidatingUniqueCategoryNameWithDuplicateInSiblings_thenThrowsException() {
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category siblingCategory = new Category("Beans", "Description", "imageUrl");
        parentCategory.addSubCategory(siblingCategory);

        assertThrows(DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(parentCategory, "Beans", null),
                "Expected DuplicateResourceException when name already exists in sibling categories.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenValidatingUniqueCategoryNameWithDuplicateInAncestors_thenThrowsException() {
        Category grandParentCategory = new Category("Coffee", "Description", "imageUrl");
        Category parentCategory = new Category("Beans", "Description", "imageUrl");
        grandParentCategory.addSubCategory(parentCategory);

        assertThrows(DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(parentCategory, "Coffee", null),
                "Expected DuplicateResourceException when name already exists in ancestor categories.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenValidatingCircularReference_thenThrowsException() {
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category subCategory = new Category("Beans", "Description", "imageUrl");
        parentCategory.addSubCategory(subCategory);
        subCategory.addSubCategory(parentCategory);

        assertThrows(CircularReferenceException.class,
                () -> categoryService.validateCircularReference(parentCategory, subCategory),
                "Expected CircularReferenceException when category creates a circular reference.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenUpdatingValidCategory_thenReturnsUpdatedCategory() {
        Long categoryId = 1L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Tea", "Updated Description", "updatedImageUrl", null);
        Category category = new Category("Coffee", "Description", "imageUrl");
        CategoryResponseDTO responseDTO =
                new CategoryResponseDTO(categoryId, "Tea", "Updated Description", "updatedImageUrl", null, Collections.emptyList(), Collections.emptyList());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any())).thenReturn(responseDTO);

        CategoryResponseDTO result = categoryService.updateCategory(categoryId, requestDTO);

        assertNotNull(result);
        assertEquals("Tea", result.getName());
        assertEquals("Updated Description", result.getDescription());

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenUpdatingNonExistingCategory_thenThrowsException() {
        Long categoryId = 1L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Tea", "Updated Description", "updatedImageUrl", null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, requestDTO));

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenUpdatingCategoryWithNonExistentParentId_thenThrowsResourceNotFoundException() {
        Long categoryId = 1L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Tea", "Updated Description", "updatedImageUrl", 999L);
        Category category = new Category("Coffee", "Description", "imageUrl");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.updateCategory(categoryId, requestDTO),
                "Expected ResourceNotFoundException when the new parent category does not exist.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenUpdatingCategoryToBeRoot_thenBecomesRootCategory() {
        Long categoryId = 2L;
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Beans", "Some Description", "someImageUrl", null);
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category subCategory = new Category("Beans", "Some Description", "someImageUrl", parentCategory);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(subCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(subCategory);
        when(categoryMapper.toResponseDTO(any())).thenReturn(
                new CategoryResponseDTO(categoryId, "Beans", "Some Description", "someImageUrl", null, Collections.emptyList(), Collections.emptyList())
        );

        CategoryResponseDTO result = categoryService.updateCategory(categoryId, requestDTO);

        assertNotNull(result);
        assertNull(result.getParentCategoryId(), "Expected category to become a root when new parent ID is null.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenUpdatingCategoryCausesCircularReference_thenThrowsException() {
        Long categoryId = 1L;
        Long subCategoryId = 2L;
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        parentCategory.setId(categoryId);
        Category subCategory = new Category("Beans", "Some Description", "someImageUrl", parentCategory);
        subCategory.setId(subCategoryId);

        parentCategory.addSubCategory(subCategory);

        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Coffee", "Updated Description", "updatedImageUrl", subCategoryId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findById(subCategoryId)).thenReturn(Optional.of(subCategory));

        assertThrows(CircularReferenceException.class,
                () -> categoryService.updateCategory(categoryId, requestDTO),
                "Expected CircularReferenceException when updating the category creates a circular reference.");

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenDeletingValidCategory_thenRemovesCategory() {
        Long categoryId = 1L;
        Category category = new Category("Coffee", "Description", "imageUrl");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).delete(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    void whenDeletingNonExistingCategory_thenThrowsException() {
        Long categoryId = 1L;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(categoryId));

        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }
}
*/
