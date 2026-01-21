package com.example.demo.service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}
