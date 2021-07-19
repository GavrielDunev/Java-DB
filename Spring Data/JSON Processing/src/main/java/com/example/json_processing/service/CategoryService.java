package com.example.json_processing.service;

import com.example.json_processing.model.dto.CategoriesByProductsDto;
import com.example.json_processing.model.entity.Category;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface CategoryService {
    void seedCategories() throws IOException;

    Set<Category> getRandomCategories();

    List<CategoriesByProductsDto> getAllOrderedByProducts();

}
