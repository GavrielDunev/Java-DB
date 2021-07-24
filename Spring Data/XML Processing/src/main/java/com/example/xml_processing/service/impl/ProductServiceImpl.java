package com.example.xml_processing.service.impl;

import com.example.xml_processing.model.dto.ProductInRangeDto;
import com.example.xml_processing.model.dto.ProductSeedDto;
import com.example.xml_processing.model.dto.ProductViewRootDto;
import com.example.xml_processing.model.entity.Product;
import com.example.xml_processing.repository.ProductRepository;
import com.example.xml_processing.service.CategoryService;
import com.example.xml_processing.service.ProductService;
import com.example.xml_processing.service.UserService;
import com.example.xml_processing.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository productRepository, ValidationUtil validationUtil, ModelMapper modelMapper, UserService userService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public void seedProducts(List<ProductSeedDto> products) throws IOException {
        products.stream()
                .filter(validationUtil::isValid)
                .map(productSeedDto -> {
                    Product product = modelMapper.map(productSeedDto, Product.class);

                    product.setSeller(this.userService.getRandomUser());

                    product.setCategories(this.categoryService.getRandomCategories());

                    if (product.getPrice().compareTo(BigDecimal.valueOf(700L)) > 0) {
                        product.setBuyer(this.userService.getRandomUser());
                    }

                    return product;
                })
                .forEach(this.productRepository::save);
    }

    @Override
    public long getCount() {
        return this.productRepository.count();
    }

    @Override
    public ProductViewRootDto findAllByPriceRange() {
        List<ProductInRangeDto> products = this.productRepository.findAllByPriceBetweenAndBuyerIsNull(BigDecimal.valueOf(500L), BigDecimal.valueOf(1000L))
                .stream()
                .map(product -> {
                    ProductInRangeDto mapped = modelMapper.map(product, ProductInRangeDto.class);
                    mapped.setSeller(product.getSeller().getFirstName() + " " + product.getSeller().getLastName());
                    return mapped;
                })
                .collect(Collectors.toList());

        ProductViewRootDto productViewRootDto = new ProductViewRootDto();
        productViewRootDto.setProducts(products);
        return productViewRootDto;
    }
}
