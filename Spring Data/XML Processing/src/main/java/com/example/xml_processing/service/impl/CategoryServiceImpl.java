package com.example.xml_processing.service.impl;

import com.example.xml_processing.model.dto.CategoriesWithProductDto;
import com.example.xml_processing.model.dto.CategoriesWithProductRootDto;
import com.example.xml_processing.model.dto.CategorySeedDto;
import com.example.xml_processing.model.entity.Category;
import com.example.xml_processing.repository.CategoryRepository;
import com.example.xml_processing.service.CategoryService;
import com.example.xml_processing.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedCategories(List<CategorySeedDto> categoriesSeedRootDto) throws IOException {
        categoriesSeedRootDto.stream()
                .filter(validationUtil::isValid)
                .map(categorySeedDto -> modelMapper.map(categorySeedDto, Category.class))
                .forEach(this.categoryRepository::save);
    }

    @Override
    public long getCountOfCategories() {
        return this.categoryRepository.count();
    }

    @Override
    public Set<Category> getRandomCategories() {
        int number = ThreadLocalRandom.current().nextInt(1, 3);
        long count = this.categoryRepository.count();
        Set<Category> categories = new HashSet<>();

        for (int i = 0; i < number; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, count + 1);
            categories.add(this.categoryRepository.findById(randomId).orElse(null));
        }

        return categories;
    }

    @Override
    public CategoriesWithProductRootDto findAllByProductCount() {
        CategoriesWithProductRootDto rootDto = new CategoriesWithProductRootDto();

        List<Category> orderedCategories = this.categoryRepository.findAllOrderByProductsCount();

        rootDto.setCategories(orderedCategories.stream()
                .map(category -> {
                    CategoriesWithProductDto mappedCategories = modelMapper.map(category, CategoriesWithProductDto.class);

                    mappedCategories.setProductsCount(category.getProducts().size());

                    mappedCategories.setAveragePrice(BigDecimal.valueOf(category.getProducts().stream()
                            .mapToDouble(product -> product.getPrice().doubleValue())
                            .average().orElse(0)));

                    mappedCategories.setTotalRevenue(BigDecimal.valueOf(category.getProducts().stream()
                            .mapToDouble(product -> product.getPrice().doubleValue())
                            .sum()));

                    return mappedCategories;
                })
                .collect(Collectors.toList()));

        return rootDto;
    }

}
