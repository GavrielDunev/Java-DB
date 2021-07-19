package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.ProductNameAndPriceDto;
import com.example.json_processing.model.dto.ProductSeedDto;
import com.example.json_processing.model.entity.Product;
import com.example.json_processing.repository.ProductRepository;
import com.example.json_processing.service.CategoryService;
import com.example.json_processing.service.ProductService;
import com.example.json_processing.service.UserService;
import com.example.json_processing.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class ProductServiceImpl implements ProductService {
    private static final String PRODUCTS_FILE_NAME = "products.json";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final UserService userService;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, Gson gson, ValidationUtil validationUtil, UserService userService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public void seedProducts() throws IOException {
        if (this.productRepository.count() > 0) {
            return;
        }

        String content = Files.readString(Path.of(RESOURCES_FILE_PATH + PRODUCTS_FILE_NAME));
        ProductSeedDto[] productSeedDtos = gson.fromJson(content, ProductSeedDto[].class);

        Arrays.stream(productSeedDtos).filter(validationUtil::isValid)
                .map(productSeedDto -> {
                    Product product = modelMapper.map(productSeedDto, Product.class);
                    product.setSeller(this.userService.getRandomUser());

                    if (product.getPrice().compareTo(BigDecimal.valueOf(700L)) > 0) {
                        product.setBuyer(this.userService.getRandomUser());
                    }

                    product.setCategories(this.categoryService.getRandomCategories());
                    return product;
                })
                .forEach(this.productRepository::save);
    }

    @Override
    public List<ProductNameAndPriceDto> findProductsInRange(BigDecimal lower, BigDecimal upper) {
        List<Product> products = this.productRepository.findAllByPriceBetweenAndBuyerIsNull(lower, upper);
        return products.stream().map(product -> {
            ProductNameAndPriceDto productNameAndPriceDto = this.modelMapper.map(product, ProductNameAndPriceDto.class);

            productNameAndPriceDto.setSeller(String.format("%s %s", product.getSeller().getFirstName(),
                    product.getSeller().getLastName()));

            return productNameAndPriceDto;
        })
                .collect(Collectors.toList());
    }
}
