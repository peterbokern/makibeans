package com.makibeans.service;

import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;
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
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory(null, "Description", "imageUrl", 1L),
                "Expected IllegalArgumentException when name is null.");
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory("", "Description", "imageUrl", 1L),
        "Expected IllegalArgumentException when name is empty");
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testCreateSubcategoryWithNullParentCategoryId() {
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory("Beans", "Subcategory", "imageUrl", null),
                "Expected IllegalArgumentException when parent category id is null.");
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void  testCreateSubcategoryWithNonExistingParentCategoryId() {
        //arrange
        when(categoryRepository.findById(10L)).thenReturn(Optional.empty());

        //act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.createSubCategory("Coffee", null, "imageUrl", 10L),
                "Expected ResourceNotFoundException when parent category does not exists.");
        verify(categoryRepository).findById(10L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testCreateNonUniqueSubcategory() {
        //arrange
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category existingSubCategory = new Category("Beans", "Subcategory", "imageUrl");
        parentCategory.addSubCategory(existingSubCategory);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        //act & assert
        assertThrows(DuplicateResourceException.class,
                () -> categoryService.createSubCategory("Beans", "Description", "imageUrl", 1L),
                "Expected DuplicateResourceException when subcategory already exists.");
        verify(categoryRepository).findById(1L);
        verifyNoMoreInteractions(categoryRepository);
    }

    //Delete category tests
    @Test
    void testDeleteCategoryWithValidCategoryId() {
        //arrange
        Category category = new Category("Coffee", "Description", "imageUrl");
        Product product1 = new Product("Coffee1", "Description", "imageUrl", category);
        Product product2 = new Product("Coffee2", "Description", "imageUrl", category);

        // Simulate Hibernate persistence: when category is added to a product. The product list in category class is updated"
        category.addProduct(product1);
        category.addProduct(product2);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        //act
        categoryService.deleteCategory(1L);

        //assert
        // Verify that findById is called twice (once in deleteCategory and once in AbstractCrudService)
        assertNull(product1.getCategory());
        assertNull(product2.getCategory());
        verify(categoryRepository, times(2)).findById(1L);
        verify(categoryRepository).delete(eq(category));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testDeleteCategoryWithNullCategoryId() {
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.deleteCategory(null),
                "Expected IllegalArgumentException when category id is null.");
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testDeleteCategoryWithNonExistingCategoryId() {
        //arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        //act & assert
        assertThrows(ResourceNotFoundException.class,
                () -> categoryService.deleteCategory(1L),
        "Expected ResourceNotFoundException when category does not exists.");
    }



  /*

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