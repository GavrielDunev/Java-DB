package com.example.springdataintro.service.impl;

import com.example.springdataintro.model.entity.Category;
import com.example.springdataintro.repository.CategoryRepository;
import com.example.springdataintro.service.CategoryService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORIES_FILE_PATH = "src/main/resources/files/categories.txt";

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void seedCategories() throws IOException {
        if (this.categoryRepository.count() > 0) {
            return;
        }

        Files.readAllLines(Path.of(CATEGORIES_FILE_PATH))
                .stream()
                .filter(row -> row.length() > 0)
                .forEach(name -> {
                    Category category = new Category(name);
                    this.categoryRepository.save(category);
                });

    }

    @Override
    public Set<Category> getRandomCategories() {
        Set<Category> categories = new HashSet<>();
        long numberOfCategories = ThreadLocalRandom.current().nextLong(1, 4);

        for (int i = 0; i < numberOfCategories; i++) {
            long id = ThreadLocalRandom.current().nextLong(1, this.categoryRepository.count() + 1);
            Category category = this.categoryRepository.findById(id).orElse(null);
            categories.add(category);
        }
        return categories;
    }
}
