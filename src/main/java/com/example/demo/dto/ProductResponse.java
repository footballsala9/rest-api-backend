package com.example.demo.dto;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, BigDecimal price, String description) {
}
