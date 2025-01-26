package com.makibeans.service;

import com.makibeans.model.Category;
import com.makibeans.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    //Create root category tests
    @Test
    void testCreateRootCategoryWithValidName() {

        //arrange
        when(categoryRepository.existsByNameAndParentCategory("Coffee", null)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Category result = categoryService.createRootCategory("Coffee", "Description", "imageUrl");

        //assert
        assertNotNull(result, "The result should not be null");
        assertEquals("Coffee", result.getName(), "Category name mismatch");
        assertNull(result.getParentCategory(), "Parent category should be null for a root category");
        assertEquals("Description", result.getDescription(), "Category description mismatch");
        assertEquals("imageUrl", result.getImageUrl(), "Image URL mismatch");
        verify(categoryRepository).existsByNameAndParentCategory(eq("Coffee"), eq(null));
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateRootCategoryWithNullOrEmptyName() {
        //act & assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createRootCategory(null, "Description", "imageUrl"));
        assertThrows(IllegalArgumentException.class, () -> categoryService.createRootCategory("", "Description", "imageUrl"));
    }

    @Test
    void testCreateDuplicateRootCategory() {
        //arrange
        when(categoryRepository.existsByNameAndParentCategory("Coffee", null)).thenReturn(true);

        //assert
        assertThrows(DuplicateResourceException.class, () -> categoryService.createRootCategory("Coffee", "Description", "imageUrl"));
        verify(categoryRepository).existsByNameAndParentCategory(eq("Coffee"), eq(null));
    }

    //create subcategory tests
    @Test
    void testCreateSubcategoryWithValidNameAndParentCategoryId() {
        //arrange
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Category result = categoryService.createSubCategory("Beans", "Subcategory", "imageUrl", 1L);

        //assert
        assertNotNull(result, "Category should not be null");
        assertEquals("Beans", result.getName(), "Category name mismatch");
        assertTrue(result.getParentCategory().getSubCategories().contains(result), "Parent category should be subcategory");
        assertEquals("Subcategory", result.getDescription(), "Category description mismatch");
        assertEquals("imageUrl", result.getImageUrl(), "Image URL mismatch");
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateSubcategoryWithNullOrEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> categoryService.createSubCategory(null, "Description", "imageUrl", 1L));
        assertThrows(IllegalArgumentException.class, () -> categoryService.createSubCategory("", "Description", "imageUrl", 1L));
    }


  /*




2. createSubCategory


    testCreateSubcategoryWithNullParentCategoryId()
    testCreateSubcategoryWithNonExistingParentCategoryId()
    testCreateNonUniqueSubcategory()

3. deleteCategory
    testDeleteCategoryWithValidCategoryId()
    testDeleteCategoryWithNullCategoryId()
    testDeleteCategoryWithNonExistingCategoryId()

4. updateCategory
    testUpdateRootCategoryWithValidCategoryIdAndNewCategoryName()
    testUpdateCategoryWithNullOrEmptyNewCategoryName()
    testUpdateCategoryWithNullCategoryId()
    testUpdateCategoryWithInvalidNewCategoryName()
    testUpdateCategoryWithNonUniqueCategoryName()
    testUpdateCategoryWithCircularReference()

5. Utility Methods
    validateCircularReference:
    testValidateCircularReferenceWithValidHierarchy()
    testValidateCircularReferenceWithCircularReference()
    validateUniqueCategoryNameWithinHierarchy:
    testValidateUniqueCategoryNameWithUniqueName()
    testValidateUniqueCategoryNameWithDuplicateNameInSiblings()
    testValidateUniqueCategoryNameWithDuplicateNameInAncestors()

*/

}