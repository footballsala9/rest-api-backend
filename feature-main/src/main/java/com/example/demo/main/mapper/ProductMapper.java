package com.example.demo.main.mapper;

import com.example.demo.common.mapper.EntityMapperConfig;
import com.example.demo.main.domain.Product;
import com.example.demo.main.dto.ProductRequest;
import com.example.demo.main.dto.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = EntityMapperConfig.class)
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    Product toEntity(ProductRequest request);

    void updateEntity(@MappingTarget Product product, ProductRequest request);
}
