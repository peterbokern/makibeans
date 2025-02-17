package com.makibeans.service;

import com.makibeans.exeptions.CircularReferenceException;
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

        //verify
        verify(categoryRepository).existsByNameAndParentCategory(eq("Coffee"), eq(null));
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testCreateRootCategoryWithNullOrEmptyName() {
        //act & assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.createRootCategory(null, "Description", "imageUrl"));
        assertThrows(IllegalArgumentException.class, () -> categoryService.createRootCategory("", "Description", "imageUrl"));

        //verify
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testCreateDuplicateRootCategory() {
        //arrange
        when(categoryRepository.existsByNameAndParentCategory("Coffee", null)).thenReturn(true);

        //assert
        assertThrows(DuplicateResourceException.class, () -> categoryService.createRootCategory("Coffee", "Description", "imageUrl"));

        //verify
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

        //verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateSubcategoryWithNullOrEmptyName() {
        //act & assert
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory(null, "Description", "imageUrl", 1L),
                "Expected IllegalArgumentException when name is null.");
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory("", "Description", "imageUrl", 1L),
        "Expected IllegalArgumentException when name is empty");

        //verify
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testCreateSubcategoryWithNullParentCategoryId() {
        //act & assert
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.createSubCategory("Beans", "Subcategory", "imageUrl", null),
                "Expected IllegalArgumentException when parent category id is null.");

        //verify
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

        //verify
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

        //verify
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
        assertNull(product1.getCategory());
        assertNull(product2.getCategory());

        //Verify that findById is called twice (once in deleteCategory and once in AbstractCrudService)
        verify(categoryRepository, times(2)).findById(1L);
        verify(categoryRepository).delete(eq(category));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testDeleteCategoryWithNullCategoryId() {
        //act & assert
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.deleteCategory(null),
                "Expected IllegalArgumentException when category id is null.");

        //verify
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

    //Update category tests
    @Test
    void testUpdateRootCategoryWithValidCategoryIdAndNewCategoryName() {
        // Arrange
        Category category = new Category("Coffee", "Description", "imageUrl");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByNameAndParentCategory("Tea", null)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Category result = categoryService.updateCategory(1L, "Tea", "otherDescription", "otherimageUrl", null);

        // Assert
        assertNotNull(result, "Category should not be null");
        assertEquals("Tea", result.getName(), "Category name mismatch");
        assertEquals("otherDescription", result.getDescription(), "Category description mismatch");
        assertEquals("otherimageUrl", result.getImageUrl(), "Category image url mismatch");
        assertNull(result.getParentCategory(), "Parent category should be null for a root category");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByNameAndParentCategory("Tea", null); // Check for the new name
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }


    @Test
    void testUpdateCategoryWithNullOrEmptyNewCategoryName() {
        //act & assert
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(2L, null, "newCategoryDescription", "newImageUrl", 1L),
                "Expected IllegalArgumentException when new category name is null.");
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(2L, "", "newCategoryDescription", "newImageUrl", 1L),
                "Expected IllegalArgumentException when new category name is empty");

        //verify
        verifyNoInteractions(categoryRepository);
    }

    @Test
   void testUpdateCategoryWithNullCategoryToUpdateId() {
        //act & assert
        assertThrows(IllegalArgumentException.class,
                () -> categoryService.updateCategory(null, "newCategoryName", "newCategoryDescription", "newImageUrl", 1L),
                "Expected IllegalArgumentException when new category name is null.");

        //verify
        verifyNoInteractions(categoryRepository);
    }

    @Test
    void testUpdateRootCategoryWithNonUniqueCategoryName() {
        //arrange
        Category categoryToUpdate = new Category("Coffee", "Description", "imageUrl");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryToUpdate));
        when(categoryRepository.existsByNameAndParentCategory("Beans", null)).thenReturn(true);

        //act & assert
        assertThrows(DuplicateResourceException.class,
                ()-> categoryService.updateCategory(1L, "Beans", "description", "imageUrl", null),
                "Expected DuplicateResourceException when root category already exists.");

        //verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).existsByNameAndParentCategory("Beans", null);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testUpdateSubCategoryWithNonUniqueCategoryNameWithinHierarchy() {
        //arrange
        Category grandParentCategory = new Category("Coffee", "Description", "imageUrl");
        Category parentCategory = new Category("Beans", "Subcategory", "imageUrl");
        Category subCategory = new Category("Strong", "Description", "imageUrl");

        grandParentCategory.addSubCategory(parentCategory);
        parentCategory.addSubCategory(subCategory);

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(subCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));

        //act & assert
        assertThrows(DuplicateResourceException.class,
                ()-> categoryService.updateCategory(3L, "Coffee", "nonUniqueCategory", "newImageUrl", 2L),
                "Expected DuplicateResourceException when category name already exists within hierarchy.");

        //verify
        verify(categoryRepository).findById(3L);
        verify(categoryRepository).findById(2L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testUpdateSubCategoryWithNonUniqueNameOutsideHierarchyAllowed() {
        //arrange
        Category parentCategoryHierachyA = new Category("Coffee", "Description", "imageUrl");
        Category subCategoryHierachyA = new Category("Strong", "Description", "imageUrl");
        parentCategoryHierachyA.addSubCategory(subCategoryHierachyA);

        Category parentCategoryHierarchyB = new Category("Tea", "Subcategory", "imageUrl");
        Category subCategoryHierarchyB = new Category("Herbal", "Description", "imageUrl");

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(subCategoryHierarchyB));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategoryHierarchyB));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //act
        Category result = categoryService.updateCategory(3L, "Strong", "description", "imageUrl", 2L);

        //assert
        assertNotNull(result, "Category should not be null");
        assertEquals("Strong", result.getName(), "Category name mismatch");
        assertEquals("description", result.getDescription(), "Category description mismatch");
        assertEquals("imageUrl", result.getImageUrl(), "Category image url mismatch");
        verify(categoryRepository).findById(3L);
        verify(categoryRepository).findById(2L);
        verify(categoryRepository).save(any(Category.class));
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testUpdateCategoryWithCircularReference() {
        //arrange
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category subCategory = new Category("Strong", "Description", "imageUrl");
        parentCategory.addSubCategory(subCategory);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(subCategory));

        //act & assert
        assertThrows(CircularReferenceException.class,
                () -> categoryService.updateCategory(1L, "Coffee", "Description", "imageUrl", 2L),
                "Expected CircularReferenceException when new parent category refers to a subcategory of itself.");

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).findById(2L);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void testValidateCircularReferenceWithValidHierarchy() {
        // Arrange: Create a valid hierarchy
        Category root = new Category("Root", "Root Description", "Root Image");
        Category parent = new Category("Beans", "Beans Description", "Beans Image", root);
        Category subCategory = new Category("Coffee", "Coffee Description", "Coffee Image", parent);

        // Act & Assert
        assertDoesNotThrow(() -> categoryService.validateCircularReference(parent, subCategory),
                "Should not throw CircularReferenceException for a valid hierarchy.");
    }



    @Test
    void testValidateCircularReferenceWithCircularReference() {
        // Arrange
        Category category = new Category("Coffee", "Description", "imageUrl");
        category.addSubCategory(category); // Self-referencing

        // Act & Assert
        assertThrows(CircularReferenceException.class,
                () -> categoryService.validateCircularReference(category, category),
                "Expected CircularReferenceException when a category is its own parent.");
    }

    @Test
    void testValidateUniqueCategoryNameWithUniqueName() {
        // Arrange
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category subCategory = new Category("Beans", "Description", "imageUrl");
        parentCategory.addSubCategory(subCategory);

        // Act & Assert (No exception expected)
        assertDoesNotThrow(() -> categoryService.validateUniqueCategoryNameWithinHierarchy(parentCategory, "Strong", null));
    }

    @Test
    void testValidateUniqueCategoryNameWithDuplicateNameInSiblings() {
        // Arrange
        Category parentCategory = new Category("Coffee", "Description", "imageUrl");
        Category siblingCategory = new Category("Beans", "Description", "imageUrl");
        parentCategory.addSubCategory(siblingCategory);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(parentCategory, "Beans", null),
                "Expected DuplicateResourceException when name already exists in sibling categories.");
    }

    @Test
    void testValidateUniqueCategoryNameWithDuplicateNameInAncestors() {
        // Arrange
        Category grandParentCategory = new Category("Coffee", "Description", "imageUrl");
        Category parentCategory = new Category("Beans", "Description", "imageUrl");
        grandParentCategory.addSubCategory(parentCategory);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(parentCategory, "Coffee", null),
                "Expected DuplicateResourceException when name already exists in ancestor categories.");
    }
}