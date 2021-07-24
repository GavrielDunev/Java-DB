package com.example.xml_processing.service;

import com.example.xml_processing.model.dto.ProductSeedDto;
import com.example.xml_processing.model.dto.ProductViewRootDto;

import java.io.IOException;
import java.util.List;


public interface ProductService {
    void seedProducts(List<ProductSeedDto> productSeedRootDto) throws IOException;

    long getCount();

    ProductViewRootDto findAllByPriceRange();
}
