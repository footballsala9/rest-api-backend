package com.example.demo.main.web;

import com.example.demo.main.dto.ProductRequest;
import com.example.demo.main.dto.ProductResponse;
import com.example.demo.main.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this test to simplify
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void getAllProducts() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Test", BigDecimal.TEN, "Desc");
        when(productService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test"));
    }

    @Test
    void getProductById() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Test", BigDecimal.TEN, "Desc");
        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void createProduct() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Test", BigDecimal.TEN, "Desc");
        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        String json = """
                {
                    "name": "Test",
                    "price": 10.00,
                    "description": "Desc"
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test"));
    }
}
