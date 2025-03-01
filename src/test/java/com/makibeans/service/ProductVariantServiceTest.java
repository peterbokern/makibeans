package com.makibeans.service;

import com.makibeans.dto.ProductVariantRequestDTO;
import com.makibeans.dto.ProductVariantUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.repository.ProductVariantRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductVariantServiceTest {

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private ProductService productService;

    @Mock
    private SizeService sizeService;

    @InjectMocks
    private ProductVariantService productVariantService;

    private ProductVariant productVariant;
    private ProductVariantRequestDTO productVariantRequestDTO;
    private ProductVariantUpdateDTO productVariantUpdateDTO;
    private Product product;
    private Size size;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", "Description", "image-url", null);
        size = new Size("Large");
        productVariant = new ProductVariant(product, size, 1000L, "SKU-1234", 10L);
        productVariantRequestDTO = new ProductVariantRequestDTO(1L, 1L,1000L, 10L);
        productVariantUpdateDTO = new ProductVariantUpdateDTO(1500L, 5L);

    }

    @AfterEach
    void tearDown() {
        productVariant = null;
        productVariantRequestDTO = null;
        productVariantUpdateDTO = null;
        product = null;
        size = null;
    }

    @Test
    void shouldCreateProductVariant_whenValidRequest() {
        // Arrange
        when(productService.findById(1L)).thenReturn(product);
        when(sizeService.findById(1L)).thenReturn(size);
        when(productVariantRepository.existsByProductAndSize(product, size)).thenReturn(false);
        when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ProductVariant result = productVariantService.createProductVariant(productVariantRequestDTO);

        // Assert
        assertNotNull(result, "The created ProductVariant should not be null.");
        assertEquals(product, result.getProduct(), "Product should match.");
        assertEquals(size, result.getSize(), "Size should match.");
        assertEquals(1000L, result.getPriceInCents(), "Price should be correct.");
        assertEquals(10L, result.getStock(), "Stock should be correct.");
        assertNotNull(result.getSku(), "SKU should be generated.");

        // Verify interactions
        verify(productService).findById(1L);
        verify(sizeService).findById(1L);
        verify(productVariantRepository).existsByProductAndSize(product, size);
        verify(productVariantRepository).save(any(ProductVariant.class));
        verifyNoMoreInteractions(productVariantRepository, productService, sizeService);
    }

    @Test
    void shouldThrowDuplicateResourceException_whenCreatingExistingProductVariant() {
        // Arrange
        when(productService.findById(1L)).thenReturn(product);
        when(sizeService.findById(1L)).thenReturn(size);
        when(productVariantRepository.existsByProductAndSize(product, size)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> productVariantService.createProductVariant(productVariantRequestDTO),
                "Expected DuplicateResourceException when creating a duplicate ProductVariant.");

        // Verify interactions
        verify(productService).findById(1L);
        verify(sizeService).findById(1L);
        verify(productVariantRepository).existsByProductAndSize(product, size);
        verifyNoMoreInteractions(productVariantRepository);
    }

    @Test
    void shouldDeleteProductVariant_whenExists() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(productVariant));

        // Act
        productVariantService.deleteProductVariant(1L);

        // Verify interactions
        verify(productVariantRepository).findById(1L);
        verify(productVariantRepository).delete(productVariant);
        verifyNoMoreInteractions(productVariantRepository);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenDeletingNonExistentProductVariant() {
        // Arrange
        when(productVariantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productVariantService.deleteProductVariant(99L),
                "Expected ResourceNotFoundException when deleting a non-existent ProductVariant.");

        // Verify interactions
        verify(productVariantRepository).findById(99L);
        verifyNoMoreInteractions(productVariantRepository);
    }

    @Test
    void shouldUpdateProductVariant_whenValidRequest() {
        // Arrange
        when(productVariantRepository.findById(1L)).thenReturn(Optional.of(productVariant));
        when(productVariantRepository.save(any(ProductVariant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Argument captor
        ArgumentCaptor<ProductVariant> productVariantCaptor = ArgumentCaptor.forClass(ProductVariant.class);

        // Act
        ProductVariant updatedVariant = productVariantService.updateProductVariant(1L, productVariantUpdateDTO);

        // Assert
        assertNotNull(updatedVariant, "Updated ProductVariant should not be null.");
        assertEquals(1500L, updatedVariant.getPriceInCents(), "Price should be updated.");
        assertEquals(5L, updatedVariant.getStock(), "Stock should be updated.");

        // Verify interactions
        verify(productVariantRepository).findById(1L);
        verify(productVariantRepository).save(productVariantCaptor.capture());

        // Capture the updated object
        ProductVariant capturedVariant = productVariantCaptor.getValue();
        assertEquals(1500L, capturedVariant.getPriceInCents(), "Captured price should be updated.");
        assertEquals(5L, capturedVariant.getStock(), "Captured stock should be updated.");

        verifyNoMoreInteractions(productVariantRepository);
    }

    @Test
    void shouldThrowResourceNotFoundException_whenUpdatingNonExistentProductVariant() {
        // Arrange
        when(productVariantRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productVariantService.updateProductVariant(99L, productVariantUpdateDTO),
                "Expected ResourceNotFoundException when trying to update a non-existent ProductVariant.");

        // Verify interactions
        verify(productVariantRepository).findById(99L);
        verifyNoMoreInteractions(productVariantRepository);
    }
}
