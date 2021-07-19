package com.example.json_processing.service;

import com.example.json_processing.model.dto.ProductNameAndPriceDto;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    void seedProducts() throws IOException;

    List<ProductNameAndPriceDto> findProductsInRange(BigDecimal lower, BigDecimal upper);
}
