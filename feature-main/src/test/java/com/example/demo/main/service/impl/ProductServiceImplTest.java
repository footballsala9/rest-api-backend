package com.example.demo.main.service.impl;

import com.example.demo.main.domain.Product;
import com.example.demo.main.dto.ProductRequest;
import com.example.demo.main.dto.ProductResponse;
import com.example.demo.main.mapper.ProductMapper;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.main.repository.ProductRepository;
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

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getAllProducts() {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"), "Description");
        ProductResponse response = new ProductResponse(1L, "Test Product", new BigDecimal("10.00"), "Description");

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        List<ProductResponse> responses = productService.getAllProducts();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Product", responses.get(0).name());
    }

    @Test
    void getProduct_Success() {
        Product product = new Product(1L, "Test Product", new BigDecimal("10.00"), "Description");
        ProductResponse response = new ProductResponse(1L, "Test Product", new BigDecimal("10.00"), "Description");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(response);

        ProductResponse actualResponse = productService.getProductById(1L);

        assertNotNull(actualResponse);
        assertEquals("Test Product", actualResponse.name());
    }

    @Test
    void getProduct_NotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void createProduct() {
        ProductRequest request = new ProductRequest("New Product", new BigDecimal("20.00"), "New Description");
        Product product = new Product(null, "New Product", new BigDecimal("20.00"), "New Description");
        Product savedProduct = new Product(1L, "New Product", new BigDecimal("20.00"), "New Description");
        ProductResponse response = new ProductResponse(1L, "New Product", new BigDecimal("20.00"), "New Description");

        when(productMapper.toEntity(request)).thenReturn(product);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toResponse(savedProduct)).thenReturn(response);

        ProductResponse actualResponse = productService.createProduct(request);

        assertNotNull(actualResponse);
        assertEquals("New Product", actualResponse.name());
        verify(productRepository).save(product);
    }

    @Test
    void updateProduct() {
        Product existingProduct = new Product(1L, "Old Name", new BigDecimal("10.00"), "Old Desc");
        ProductRequest request = new ProductRequest("New Name", new BigDecimal("15.00"), "New Desc");
        Product updatedProduct = new Product(1L, "New Name", new BigDecimal("15.00"), "New Desc");
        ProductResponse response = new ProductResponse(1L, "New Name", new BigDecimal("15.00"), "New Desc");

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            ProductRequest r = invocation.getArgument(1);
            p.setName(r.name());
            p.setPrice(r.price());
            p.setDescription(r.description());
            return null;
        }).when(productMapper).updateEntity(existingProduct, request);
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toResponse(updatedProduct)).thenReturn(response);

        ProductResponse actualResponse = productService.updateProduct(1L, request);

        assertEquals("New Name", actualResponse.name());
        assertEquals(new BigDecimal("15.00"), actualResponse.price());
    }

    @Test
    void deleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }
}
