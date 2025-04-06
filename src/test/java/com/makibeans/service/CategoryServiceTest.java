package com.makibeans.service;

import com.makibeans.dto.category.CategoryRequestDTO;
import com.makibeans.dto.category.CategoryResponseDTO;
import com.makibeans.dto.category.CategoryUpdateDTO;
import com.makibeans.exceptions.CategoryInUseException;
import com.makibeans.exceptions.CircularReferenceException;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.CategoryMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.CategoryRepository;
import com.makibeans.util.ImageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CategoryService
 */

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @Mock private ProductService productService;
    @Mock private ImageUtils imageUtils;

    @InjectMocks private CategoryService categoryService;

    private Category rootCategory;
    private Category subCategory;

    @BeforeEach
    void setUp() {
        rootCategory = new Category("Coffee", "Rich coffee flavors");
        subCategory = new Category("Beans", "Arabica and Robusta");
        subCategory.setParentCategory(rootCategory);
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateRootCategory_When_ValidInput() {
        // Arrange
        CategoryRequestDTO request = new CategoryRequestDTO("Coffee", "Rich", null);
        Category savedCategory = new Category("coffee", "rich");
        CategoryResponseDTO expectedResponseDTO = new CategoryResponseDTO(1L, "coffee", "rich", null, null, List.of(), List.of());

        when(categoryRepository.existsByNameAndParentCategory("coffee", null)).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toResponseDTO(savedCategory)).thenReturn(expectedResponseDTO);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(request);

        // Assert
        assertNotNull(result, "Expected category response not to be null");
        assertEquals(expectedResponseDTO, result, "Expected category name to be 'coffee'");

        // Verify
        verify(categoryRepository).existsByNameAndParentCategory("coffee", null);
        verify(categoryRepository).save(any(Category.class));
        verify(categoryMapper).toResponseDTO(savedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_RootCategoryAlreadyExists() {
        // Arrange
        CategoryRequestDTO request = new CategoryRequestDTO("Coffee", "Rich", null);
        when(categoryRepository.existsByNameAndParentCategory("coffee", null)).thenReturn(true);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.createCategory(request),
                "Expected DuplicateResourceException");

        // Verify
        verify(categoryRepository).existsByNameAndParentCategory("coffee", null);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_CreateSubCategory_When_ValidParent() {
        // Arrange
        CategoryRequestDTO request = new CategoryRequestDTO("Beans", "Roasted", 1L);
        Category savedCategory = new Category("beans", "roasted");
        savedCategory.setParentCategory(rootCategory);
        CategoryResponseDTO expectedResponseDTO = new CategoryResponseDTO(2L, "beans", "roasted", null, 1L, List.of(), List.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));
        when(categoryRepository.save(any())).thenReturn(savedCategory);
        when(categoryMapper.toResponseDTO(savedCategory)).thenReturn(expectedResponseDTO);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(request);

        // Assert
        assertNotNull(result, "Expected category response not to be null");
        assertEquals("beans", result.getName(), "Expected subcategory name to be 'beans'");
        assertEquals(1L, result.getParentCategoryId(), "Expected parent category ID to be 1");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any());
        verify(categoryMapper).toResponseDTO(savedCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_ThrowCategoryInUseException_When_CategoryHasProducts() {
        // Arrange
        rootCategory.setId(1L);
        Product product = mock(Product.class);
        when(productService.getProductsByCategoryId(1L)).thenReturn(List.of(product));
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(List.of());

        // Act & Assert
        assertThrows(
                CategoryInUseException.class,
                () -> categoryService.deleteCategory(1L),
                "Expected CategoryInUseException when products exist");

        // Verify
        verify(productService).getProductsByCategoryId(1L);
        verify(categoryRepository).findByParentCategoryId(1L);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateCategoryNameAndDescription() {
        // Arrange
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO("Updated", "New desc", null);
        CategoryResponseDTO expectedResponseDTO = new CategoryResponseDTO(1L, "updated", "new desc", null, null, List.of(), List.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));
        when(categoryRepository.save(rootCategory)).thenReturn(rootCategory);
        when(categoryMapper.toResponseDTO(rootCategory)).thenReturn(expectedResponseDTO);

        // Act
        CategoryResponseDTO result = categoryService.updateCategory(1L, updateDTO);

        // Assert
        assertNotNull(result, "Expected updated category response not to be null");
        assertEquals("updated", result.getName(), "Expected updated name to be 'updated'");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(rootCategory);
        verify(categoryMapper).toResponseDTO(rootCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonExistentCategory() {
        // Arrange
        CategoryUpdateDTO updateDTO = new CategoryUpdateDTO("Name", "Desc", null);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.updateCategory(99L, updateDTO),
                "Expected ResourceNotFoundException");

        // Verify
        verify(categoryRepository).findById(99L);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    // ========================================
    // VALIDATION
    // ========================================

    @Test
    void should_ThrowCircularReferenceException_When_CircularReferenceDetected() {
        // Arrange
        Category root = new Category("Root", "Top level");
        Category child = new Category("Child", "Child category");
        child.setParentCategory(root);
        root.getSubCategories().add(child);
        root.setParentCategory(child); // create circular reference

        // Act & Assert
        assertThrows(
                CircularReferenceException.class, () ->
                categoryService.validateCircularReference(child, root),
                "Expected CircularReferenceException for circular reference");

        // Verify
        verifyNoInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_DuplicateNameInSubCategories() {
        // Arrange
        Category duplicate = new Category("Beans", "Desc");
        rootCategory.getSubCategories().add(duplicate);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(rootCategory, "Beans", null),
                "Expected DuplicateResourceException");

        // Verify
        verifyNoInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_DuplicateNameInHierarchy() {
        // Arrange
        rootCategory.setName("Coffee");
        subCategory.setName("Sub");
        subCategory.setParentCategory(rootCategory);

        // Act & Assert
        assertThrows(
                DuplicateResourceException.class,
                () -> categoryService.validateUniqueCategoryNameWithinHierarchy(subCategory, "Coffee", null),
                "Expected DuplicateResourceException");
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnCategoryResponseDTO_When_IdExists() {
        // Arrange
        rootCategory.setId(1L);
        CategoryResponseDTO expectedResponseDTO = new CategoryResponseDTO(1L, "coffee", "desc", null, null, List.of(), List.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));
        when(categoryMapper.toResponseDTO(rootCategory)).thenReturn(expectedResponseDTO);

        // Act
        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertEquals("coffee", result.getName(), "Expected category name to be 'coffee'");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(categoryMapper).toResponseDTO(rootCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_IdInvalid() {
        // Arrange
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryById(99L),
                "Expected ResourceNotFoundException");

        // Verify
        verify(categoryRepository).findById(99L);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    // ========================================
    // IMAGE HANDLING
    // ========================================

    @Test
    void should_UploadImage_When_ValidCategoryAndImage() {
        // Arrange
        rootCategory.setId(1L);
        MultipartFile mockImage = mock(MultipartFile.class);
        byte[] imageBytes = new byte[]{1, 2, 3};
        CategoryResponseDTO expectedResponseDTO = new CategoryResponseDTO(1L, "coffee", "desc", null, null, List.of(), List.of());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));
        when(imageUtils.validateAndExtractImageBytes(mockImage)).thenReturn(imageBytes);
        when(categoryRepository.save(rootCategory)).thenReturn(rootCategory);
        when(categoryMapper.toResponseDTO(rootCategory)).thenReturn(expectedResponseDTO);

        // Act
        CategoryResponseDTO result = categoryService.uploadCategoryImage(1L, mockImage);

        // Assert
        assertNotNull(result, "Expected uploaded image result not to be null");
        assertEquals(expectedResponseDTO, result, "Expected category response to match");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(imageUtils).validateAndExtractImageBytes(mockImage);
        verify(categoryRepository).save(rootCategory);
        verify(categoryMapper).toResponseDTO(rootCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_DeleteCategoryImage_When_ImageExists() {
        // Arrange
        rootCategory.setId(1L);
        rootCategory.setImage(new byte[]{1, 2, 3});
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));
        when(categoryRepository.save(rootCategory)).thenReturn(rootCategory);

        // Act
        categoryService.deleteCategoryImage(1L);

        // Assert
        assertNull(rootCategory.getImage(), "Expected category image to be null after deletion");

        // Verify
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(rootCategory);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ReturnCategoryImage_When_ImageExists() {
        // Arrange
        byte[] imageBytes = new byte[]{1, 2, 3};
        rootCategory.setId(1L);
        rootCategory.setImage(imageBytes);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));

        // Act
        byte[] result = categoryService.getCategoryImage(1L);

        // Assert
        assertNotNull(result, "Expected image bytes not to be null");
        assertArrayEquals(imageBytes, result, "Expected image bytes to match");

        // Verify
        verify(categoryRepository).findById(1L);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_GetCategoryImageButImageIsNull() {
        // Arrange
        rootCategory.setImage(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(rootCategory));

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.getCategoryImage(1L),
                "Expected ResourceNotFoundException when image is null");

        // Verify
        verify(categoryRepository).findById(1L);
        verifyNoMoreInteractions(categoryRepository, categoryMapper, productService, imageUtils);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_FilterCategories_ByName() {
        // Arrange
        Map<String, String> params = Map.of("name", "espresso");
        Category category = new Category("Espresso", "Strong coffee");
        CategoryResponseDTO expectedDTO = new CategoryResponseDTO(1L, "espresso", "strong coffee", null, null, List.of(), List.of());

        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(expectedDTO);

        // Act
        List<CategoryResponseDTO> result = categoryService.findBySearchQuery(params);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Expected one matching category from filter");
        assertEquals(expectedDTO, result.get(0), "Returned category should match expected DTO");

        // Verify
        verify(categoryRepository).findAll();
        verify(categoryMapper).toResponseDTO(category);
        verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }
}
