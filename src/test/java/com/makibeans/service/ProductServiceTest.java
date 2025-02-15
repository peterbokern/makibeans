package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    // Create Product Tests
    @Test
    void testCreateProductWithValidData() {
        // Arrange
        Category category = new Category("Coffee", "Description", "imageUrl");

        when(categoryService.findById(1L)).thenReturn(category);
        when(productRepository.findByProductName("Espresso")).thenReturn(null);  // No duplicate found
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.createProduct("Espresso", "Rich flavor", 1L, "imageUrl");

        // Assert
        assertNotNull(result, "Product should not be null");
        assertEquals("Espresso", result.getProductName(), "Product name mismatch");
        assertEquals("Rich flavor", result.getProductDescription(), "Product description mismatch");
        assertEquals(category, result.getCategory(), "Category mismatch");
        assertEquals("imageUrl", result.getProductImageUrl(), "Image URL mismatch");

        // Verify
        verify(categoryService).findById(1L);
        verify(productRepository).findByProductName("Espresso");
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testCreateProductWithNullProductName() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct(null, "Description", 1L, "imageUrl"),
                "Expected IllegalArgumentException when product name is null");

        verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProductWithEmptyProductName() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct("", "Description", 1L, "imageUrl"),
                "Expected IllegalArgumentException when product name is empty");

        verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProductWithNullDescription() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct("productName", null, 1L, "imageUrl"),
                "Expected IllegalArgumentException when product description is null");

        verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProductWithEmptyDescription() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct("productName", "", 1L, "imageUrl"),
                "Expected IllegalArgumentException when product description is empty");

        verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProductWithNullCategoryId() {
        assertThrows(IllegalArgumentException.class,
                () -> productService.createProduct("productName", "description", null, "imageUrl"),
                "Expected IllegalArgumentException when category ID is null");

        verifyNoInteractions(productRepository);
    }

    @Test
    void testCreateProductWithDuplicateName() {
        // Arrange
        Product duplicateProduct = new Product("Espresso", "Description", "imageUrl", new Category("Coffee", "Description", "imageUrl"));
        when(productRepository.findByProductName("Espresso")).thenReturn(duplicateProduct);  // Mock duplicate product

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> productService.createProduct("Espresso", "Description", 1L, "imageUrl"),
                "Expected DuplicateResourceException when a product with the same name exists");

        // Verify
        verify(productRepository).findByProductName("Espresso");
        verifyNoMoreInteractions(productRepository);
    }

    // Delete Product Tests
    @Test
    void testDeleteProductWithValidId() {
        //Arrange
        Product product = new Product("name", "description", "imageUrl", new Category("Coffee", "Description", "imageUrl"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1L);

        // Verify
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void testDeleteProductWithNullId() {
        //act & assert
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(null), "Should throw IllegalArgumentException for null id");

        //verify
        verifyNoInteractions(productRepository);

    }

    @Test
    void testDeleteProductWithInvalidId() {
        //arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        //act & assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));

        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);

    }

    @Test
    void testUpdateProductWithValidData() {
        // Arrange
        Category category = new Category("Coffee", "Description", "imageUrl");
        Product existingProduct = new Product("Espresso", "Rich flavor", "imageUrl", category);

        // Simulate JPA-generated ID using a spy
        Product existingProductSpy = Mockito.spy(existingProduct);
        when(existingProductSpy.getId()).thenReturn(1L);

        when(categoryService.findById(1L)).thenReturn(category);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProductSpy));
        when(productRepository.findByProductName("Latte")).thenReturn(null);  // No duplicate found
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product updatedProduct = productService.updateProduct(1L, "Latte", "Smooth taste", 1L, "newImageUrl");

        // Assert
        assertNotNull(updatedProduct, "Updated product should not be null");
        assertEquals("Latte", updatedProduct.getProductName(), "Updated product name mismatch");
        assertEquals("Smooth taste", updatedProduct.getProductDescription(), "Updated product description mismatch");
        assertEquals("newImageUrl", updatedProduct.getProductImageUrl(), "Updated product image URL mismatch");

        // Verify
        verify(categoryService).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).findByProductName("Latte");
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);  // Now all interactions are accounted for
    }

    @Test
    void testUpdateProductWithNonExistingProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, "Latte", "Smooth taste", 1L, "newImageUrl"));
        verify(productRepository).findById(1L);
    }

    @Test
    void testUpdateProductWithDuplicateName() {
        //arrange
        Category category = new Category("Coffee", "Description", "imageUrl");
        Product existingProduct = new Product("Espresso", "Rich flavor", "imageUrl", category);
        Product duplicateProduct = new Product("Espresso", "Another flavor", "imageUrl", category);

        // Use spy to mock the getId(), since ID is set by JPA it will return null giving nullpointer exception
        Product existingProductSpy = Mockito.spy(existingProduct);
        Product duplicateProductSpy = Mockito.spy(duplicateProduct);
        when(existingProductSpy.getId()).thenReturn(1L);
        when(duplicateProductSpy.getId()).thenReturn(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProductSpy));
        when(productRepository.findByProductName("Espresso")).thenReturn(duplicateProductSpy);

        //act & assert
        assertThrows(DuplicateResourceException.class,
                () -> productService.updateProduct(1L, "Espresso", "Smooth taste", 1L, "newImageUrl"));

        //verify
        verify(productRepository).findById(1L);
        verify(productRepository).findByProductName("Espresso");
    }

}
