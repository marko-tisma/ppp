package com.mtisma.ppp.controller;

import com.mtisma.ppp.model.Category;
import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.service.CategoryService;
import com.mtisma.ppp.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("")
    public List<Category> getAll() {
        return categoryService.findAll();
    }

    @GetMapping("/{id}")
    public List<Product> getById(
        @PathVariable("id") long id,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String nameQuery
    ) {
        return productService.findByCategoryId(
            id,
            Optional.ofNullable(page),
            Optional.ofNullable(size),
            Optional.ofNullable(sortBy),
            Optional.ofNullable(nameQuery)
        );
    }

}
