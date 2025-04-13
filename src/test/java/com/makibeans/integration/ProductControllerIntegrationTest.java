package com.makibeans.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makibeans.dto.product.ProductRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "classpath:test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // ===================================
    // GET /products - List All Products
    // ===================================
    @Test
    void should_GetAllProducts_WhenPublic() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(1));

    }

    // ===================================
    // GET /products/{id} - Product Detail
    // ===================================
    @Test
    void should_GetProductById_WhenPublic() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("ethiopian dark roast"))
                .andExpect(jsonPath("$.description").value("bold and fruity beans from ethiopia, perfect for espresso lovers."))
                .andExpect(jsonPath("$.imageUrl").value("null"))
                .andExpect(jsonPath("$.categoryId").value(7))
                .andExpect(jsonPath("$.categoryName").value("dark roast"))
                .andExpect(jsonPath("$.productVariants", hasSize(2)))
                .andExpect(jsonPath("$.productAttributes", hasSize(3)));
    }

    // ===================================
    // POST /products - Create Product
    // ===================================
    @Test
    @WithMockUser(username = "maki_admin", roles = "ADMIN")
    void should_CreateProduct_WhenAdmin() throws Exception {
        // Arrange
        ProductRequestDTO request = new ProductRequestDTO("Test Product", "This is a test product", 1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test product"))
                .andExpect(jsonPath("$.description").value("this is a test product"))
                .andExpect(jsonPath("$.categoryId").value(1))
                .andExpect(jsonPath("$.categoryName").value("coffee"))
                .andExpect(jsonPath("$.productVariants", hasSize(0)))
                .andExpect(jsonPath("$.productAttributes", hasSize(0)))
                .andExpect(jsonPath("$.imageUrl").value("null"));
    }

    // ===================================
    // PUT /products/{id} - Update Product
    // ===================================
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void should_UpdateProduct_WhenAdmin() throws Exception {
        // Arrange
        ProductRequestDTO update = new ProductRequestDTO("Updated Name", "Updated Description", 1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"))
                .andExpect(jsonPath("$.description").value("updated description"));
    }

    // ===================================
    // DELETE /products/{id}
    // ===================================
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void should_DeleteProduct_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(status().isNoContent());
    }

    // ===================================
    // POST /products - Forbidden for USER
    // ===================================
    @Test
    @WithMockUser(username = "regular_user", roles = "USER")
    void should_ReturnForbidden_WhenNotAdmin() throws Exception {
        // Arrange
        ProductRequestDTO request = new ProductRequestDTO("Name", "Description", 1L);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    // ===================================
    // POST /products/{id}/image
    // ===================================
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void should_UploadImage_WhenAdmin() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy image content".getBytes());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.multipart("/products/1/image").file(image))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Upload-Message", containsString("uploaded successfully")))
                .andExpect(jsonPath("$.imageUrl").value("/products/1/image"));
    }

    // ===================================
    // GET /products/{id}/image
    // ===================================
    @Test
    @WithMockUser(username = "maki_admin", roles = "ADMIN")
    void should_GetProductImage_WhenUploaded() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "dummy image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/products/1/image").file(image))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"))
                .andExpect(content().bytes("dummy image content".getBytes()));
    }

    // ===================================
    // DELETE /products/{id}/image
    // ===================================
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void should_DeleteImage_WhenAdmin() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1/image"))
                .andExpect(status().isNoContent());
    }

    // ===================================
    // FILTER: search=kenya
    // ===================================
    @Test
    void should_FilterProducts_ByQueryMatch() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("search", "kenya"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", containsStringIgnoringCase("kenya")));
    }

    // ===================================
    // FILTER: origin=ethiopia
    // ===================================
    @Test
    void should_FilterProducts_ByAttribute() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("origin", "ethiopia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("ethiopian dark roast"));
    }

    // ===================================
    // FILTER: edge case – unknown attribute
    // ===================================
    @Test
    void should_ReturnEmpty_WhenFilterByUnknownAttribute() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("flavor", "bubblegum"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ===================================
    // FILTER: invalid numeric range
    // ===================================
    @Test
    void should_ReturnEmpty_WhenFilterWithUnmatchedPriceRange() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("minPrice", "999999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    // ===================================
    // FILTER: multiple attributes match
    // ===================================
    @Test
    void should_FilterProducts_ByMultipleAttributes() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
                        .param("origin", "ethiopia")
                        .param("intensity", "strong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("ethiopian dark roast"));
    }
}
