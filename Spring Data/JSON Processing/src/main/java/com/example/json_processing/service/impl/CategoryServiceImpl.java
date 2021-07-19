package com.example.json_processing.service.impl;

import com.example.json_processing.model.dto.CategoriesByProductsDto;
import com.example.json_processing.model.dto.CategorySeedDto;
import com.example.json_processing.model.entity.Category;
import com.example.json_processing.repository.CategoryRepository;
import com.example.json_processing.service.CategoryService;
import com.example.json_processing.util.ValidationUtil;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.json_processing.constant.GlobalConstants.RESOURCES_FILE_PATH;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORIES_FILE_NAME = "categories.json";

    private final CategoryRepository categoryRepository;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, Gson gson, ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedCategories() throws IOException {
        if (this.categoryRepository.count() > 0) {
            return;
        }

        String content = Files.readString(Path.of(RESOURCES_FILE_PATH + CATEGORIES_FILE_NAME));
        CategorySeedDto[] categorySeedDtos = gson.fromJson(content, CategorySeedDto[].class);
        Arrays.stream(categorySeedDtos).filter(validationUtil::isValid)
                .map(categorySeedDto -> modelMapper.map(categorySeedDto, Category.class))
                .forEach(this.categoryRepository::save);
    }

    @Override
    public Set<Category> getRandomCategories() {
        Set<Category> randomCategories = new HashSet<>();
        int number = ThreadLocalRandom.current().nextInt(1, 3);
        long categoryCount = this.categoryRepository.count();

        for (int i = 0; i < number; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, categoryCount + 1);
            randomCategories.add(this.categoryRepository.findById(randomId).orElse(null));
        }

        return randomCategories;
    }

    @Override
    public List<CategoriesByProductsDto> getAllOrderedByProducts() {
        return this.categoryRepository.findAllOrderByProductsCount()
                .stream()
                .map(category -> {
                    CategoriesByProductsDto categoriesByProductsDto = modelMapper.map(category, CategoriesByProductsDto.class);

                    categoriesByProductsDto.setProductsCount(category.getProducts().size());

                    categoriesByProductsDto.setAveragePrice(BigDecimal.valueOf(category.getProducts()
                            .stream()
                            .mapToDouble(c -> Double.parseDouble(String.valueOf(c.getPrice())))
                            .average().orElse(0)));

                    categoriesByProductsDto.setTotalRevenue(BigDecimal.valueOf(category.getProducts()
                            .stream()
                            .mapToDouble(c -> Double.parseDouble(String.valueOf(c.getPrice())))
                            .sum()));

                    return categoriesByProductsDto;
                })
                .collect(Collectors.toList());
    }
}
