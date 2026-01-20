package com.example.demo.service.impl;

import com.example.demo.domain.Product;
import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts() {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"), "Description");
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductResponse> responses = productService.getAllProducts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Product", responses.get(0).name());
    }

    @Test
    void getProductById_Success() {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"), "Description");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertNotNull(response);
        assertEquals("Test Product", response.name());
    }

    @Test
    void getProductById_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct() {
        ProductRequest request = new ProductRequest("New Product", new BigDecimal("20.00"), "New Description");
        Product savedProduct = new Product(1L, "New Product", new BigDecimal("20.00"), "New Description");

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals("New Product", response.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct() {
        Product existingProduct = new Product(1L, "Old Name", new BigDecimal("10.00"), "Old Desc");
        ProductRequest request = new ProductRequest("New Name", new BigDecimal("15.00"), "New Desc");
        Product updatedProduct = new Product(1L, "New Name", new BigDecimal("15.00"), "New Desc");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(1L, request);

        assertEquals("New Name", response.name());
        assertEquals(new BigDecimal("15.00"), response.price());
    }

    @Test
    void deleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
}
