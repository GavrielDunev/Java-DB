package com.example.xml_processing.service;

import com.example.xml_processing.model.dto.CategoriesWithProductRootDto;
import com.example.xml_processing.model.dto.CategorySeedDto;
import com.example.xml_processing.model.entity.Category;


import java.io.IOException;
import java.util.List;
import java.util.Set;


public interface CategoryService {
    void seedCategories(List<CategorySeedDto> categoriesSeedRootDto) throws IOException;

    long getCountOfCategories();

    Set<Category> getRandomCategories();

    CategoriesWithProductRootDto findAllByProductCount();
}
