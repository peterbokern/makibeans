package com.makibeans.service;

import com.makibeans.dto.product.ProductPageDTO;
import com.makibeans.dto.product.ProductRequestDTO;
import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.dto.product.ProductUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.model.ProductAttribute;
import com.makibeans.repository.ProductRepository;
import com.makibeans.util.ImageUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    CategoryService categoryService;
    @Mock
    AttributeTemplateService attributeTemplateService;
    @Mock
    ProductMapper productMapper;
    @Mock
    ProductAttributeService productAttributeService;
    @Mock
    ImageUtils imageUtils;

    @InjectMocks
    ProductService productService;

    Product product;
    Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Coffee", "Rich flavor");
        product = new Product("Espresso", "Smooth and rich", null, category);
    }

    @AfterEach
    void tearDown() {
        product = null;
        category = null;
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateProduct_When_ValidInput() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("Espresso", "Smooth", 1L);
        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(
                1L, "Espresso", "Smooth and rich", null, 1L, "Coffee",
                List.of(), List.of()
        );

        when(productRepository.existsByProductName("Espresso")).thenReturn(false);
        when(categoryService.findById(1L)).thenReturn(category);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponseDTO);

        // Act
        ProductResponseDTO actualResponseDTO = productService.createProduct(requestDTO);

        // Assert
        assertNotNull(actualResponseDTO, "The returned ProductResponseDTO should not be null");
        assertEquals(expectedResponseDTO, actualResponseDTO, "Expected the returned ProductResponseDTO to match the mapped one");

        // Verify
        verify(productRepository).existsByProductName("Espresso");
        verify(categoryService).findById(1L);
        verify(productRepository).save(any());
        verify(productMapper).toResponseDTO(product);
        verifyNoMoreInteractions(productRepository, categoryService, productMapper);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_NameAlreadyExists() {
        // Arrange
        ProductRequestDTO requestDTO = new ProductRequestDTO("Espresso", "Smooth", 1L);
        when(productRepository.existsByProductName("Espresso")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> productService.createProduct(requestDTO));

        // Verify
        verify(productRepository).existsByProductName("Espresso");
        verifyNoMoreInteractions(productRepository);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteProduct_When_IdExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductAttribute productAttribute = mock(ProductAttribute.class);
        when(productAttribute.getId()).thenReturn(99L);
        when(productAttributeService.getProductAttributesByProductId(1L)).thenReturn(List.of(productAttribute));

        // Act
        productService.deleteProduct(1L);

        // Verify
        verify(productRepository).findById(1L);
        verify(productAttributeService).getProductAttributesByProductId(1L);
        verify(productAttributeService).deleteProductAttribute(99L);
        verify(productRepository).delete(product);
        verifyNoMoreInteractions(productRepository, productAttributeService);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_DeletingNonexistentProduct() {
        // Arrange
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.deleteProduct(99L),
                "Expected ResourceNotFoundException when deleting a non-existent product");

        // Verify
        verify(productRepository).findById(99L);
        verifyNoMoreInteractions(productRepository);
    }

    // ========================================
    // GET BY ID
    // ========================================

    @Test
    void should_ReturnProduct_When_IdExists() {
        // Arrange
        ProductResponseDTO expectedResponse = new ProductResponseDTO(1L, "Espresso", "Smooth and rich", null, 1L, "Coffee", List.of(), List.of());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponse);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponse);

        // Act
        ProductResponseDTO actualResponse = productService.getProductById(1L);

        // Assert
        assertNotNull(actualResponse, "Returned ProductResponseDTO should not be null");
        assertEquals(expectedResponse, actualResponse, "Expected the actual ProductResponseDTO to match the mapped response");

        // Verify
        verify(productRepository).findById(1L);
        verify(productMapper).toResponseDTO(product);
        verifyNoMoreInteractions(productRepository, productMapper);
    }


    @Test
    void should_ReturnProducts_When_CategoryIdExists() {
        // Arrange
        when(productRepository.findProductsByCategoryId(1L)).thenReturn(List.of(product));

        // Act
        List<Product> result = productService.getProductsByCategoryId(1L);

        // Assert
        assertEquals(1, result.size());
        verify(productRepository).findProductsByCategoryId(1L);
    }


    @Test
    void should_ThrowResourceNotFoundException_When_ProductNotFound() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));

        // Verify
        verify(productRepository).findById(1L);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateProduct_When_ValidChanges() {
        // Arrange
        ProductUpdateDTO updateDTO = new ProductUpdateDTO("Latte", "Tasty", null, 1L);
        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.existsByProductName("Latte")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponseDTO);

        // Act
        ProductResponseDTO actualResponseDTO = productService.updateProduct(1L, updateDTO);

        // Assert
        assertNotNull(actualResponseDTO);
        assertEquals(expectedResponseDTO, actualResponseDTO);

        // Verify
        verify(productRepository).findById(1L);
        verify(productRepository).existsByProductName("Latte");
        verify(productRepository).save(any(Product.class));
        verify(productMapper).toResponseDTO(product);
        verifyNoMoreInteractions(productRepository, productMapper);
    }

    @Test
    void should_NotUpdateProduct_When_NoFieldsChanged() {
        // Arrange
        ProductUpdateDTO updateDTO = new ProductUpdateDTO("Espresso", "Smooth and rich", null, category.getId());
        Product product = new Product("Espresso", "Smooth and rich", null, category);
        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(1L, "Espresso", "Smooth and rich", null, 1L, "Coffee", List.of(), List.of());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponseDTO);

        // Act
        ProductResponseDTO actualResponseDTO = productService.updateProduct(1L, updateDTO);

        // Assert
        assertNotNull(actualResponseDTO, "Expected result to not be null when fields are unchanged");
        assertEquals(expectedResponseDTO, actualResponseDTO, "Expected the returned ProductResponseDTO to match the mapped one");

        // Verify
        verify(productRepository).findById(1L);
        verify(productMapper).toResponseDTO(product);
        verifyNoMoreInteractions(productRepository, productMapper);
    }


    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonexistentProduct() {
        // Arrange
        ProductUpdateDTO updateDTO = new ProductUpdateDTO("Latte", "Tasty", null, 1L);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> productService.updateProduct(1L, updateDTO),
                "Expected ResourceNotFoundException when trying to update a non-existent product");

        // Verify
        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    // ========================================
    // IMAGE HANDLING
    // ========================================

    @Test
    void should_ReturnProductImage_When_ImageExists() {
        // Arrange
        byte[] imageData = new byte[]{1, 2, 3};
        product.setProductImage(imageData);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        byte[] result = productService.getProductImage(1L);

        // Assert
        assertNotNull(result, "Returned byte array should not be null");
        assertArrayEquals(imageData, result, "Expected the returned byte array to match the image data");

        // Verify
        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void should_UploadImage_When_Valid() throws Exception {
        // Arrange
        MultipartFile image = mock(MultipartFile.class);
        byte[] bytes = new byte[]{1, 2, 3};

        ProductResponseDTO expectedResponseDTO = new ProductResponseDTO(
                1L, "Espresso", "Smooth and rich", null, 1L, "Coffee", List.of(), List.of());

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(imageUtils.validateAndExtractImageBytes(image)).thenReturn(bytes);
        when(productRepository.save(any())).thenReturn(product);
        when(productMapper.toResponseDTO(product)).thenReturn(expectedResponseDTO);

        // Act
        ProductResponseDTO result = productService.uploadProductImage(1L, image);

        // Assert
        assertNotNull(result, "Returned ProductResponseDTO should not be null");
        assertEquals(expectedResponseDTO, result, "Expected the returned ProductResponseDTO to match");
        assertArrayEquals(bytes, product.getProductImage(), "Expected image bytes to be set correctly on the product");

        // Verify
        verify(productRepository).findById(1L);
        verify(imageUtils).validateAndExtractImageBytes(image);
        verify(productRepository).save(product);
        verify(productMapper).toResponseDTO(product);
        verifyNoMoreInteractions(productRepository, productMapper, imageUtils);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_GettingMissingImage() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductImage(1L),
                "Expected ResourceNotFoundException when getting a missing image");

        // Verify
        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void should_DeleteProductImage() {
        // Arrange
        product.setProductImage(new byte[]{1, 2, 3});
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProductImage(1L);

        // Verify
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
        verifyNoMoreInteractions(productRepository);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_ReturnFilteredProducts_When_ValidFilters() {
        // Arrange
        Map<String, String> filters = Map.of("query", "espresso");
        Product espresso = new Product("Espresso", "Strong coffee", null, category);
        ReflectionTestUtils.setField(espresso, "id", 1L);
        ProductResponseDTO responseDTO = new ProductResponseDTO(1L, "Espresso", "Strong coffee", null, null, null, List.of(), List.of());

        when(productRepository.findAll()).thenReturn(List.of(espresso));
        when(attributeTemplateService.getValidAttributeKeys()).thenReturn(Set.of());
        when(productMapper.toResponseDTO(espresso)).thenReturn(responseDTO);

        // Act
        ProductPageDTO result = productService.findBySearchQuery(filters);

        // Assert
        assertNotNull(result, "The ProductPageDTO result should not be null");
        assertEquals(1, result.getContent().size(), "Expected one product in the filtered result");
        assertEquals(responseDTO, result.getContent().get(0), "Expected the product in the result to match the mapped DTO");

        // Verify
        verify(productRepository).findAll();
        verify(attributeTemplateService).getValidAttributeKeys();
        verify(productMapper).toResponseDTO(espresso);
        verifyNoMoreInteractions(productRepository, attributeTemplateService, productMapper, productAttributeService, imageUtils);
    }
}
