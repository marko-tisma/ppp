package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Category;
import com.mtisma.ppp.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        this.categoryRepository.findAll().forEach(categories::add);
        return categories;
    }

    public Optional<Category> findById(long id) {
        return this.categoryRepository.findById(id);
    }
}
