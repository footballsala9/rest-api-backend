package com.example.demo.dto;

import java.math.BigDecimal;

public record ProductRequest(String name, BigDecimal price, String description) {
}
