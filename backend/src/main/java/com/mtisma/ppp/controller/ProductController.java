package com.mtisma.ppp.controller;

import com.mtisma.ppp.model.PriceHistory;
import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.model.Specification;
import com.mtisma.ppp.service.ProductService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("")
    public List<Product> getAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public Product getById(@PathVariable("id") long id) {
        return productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/history")
    public List<PriceHistory> getHistoryById(
            @PathVariable("id") long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return productService.getProductHistory(id, Optional.ofNullable(from), Optional.ofNullable(to));
    }

    @GetMapping("/{id}/specifications")
    public List<Specification> getSpecificationsById(@PathVariable("id") long id) {
        return productService.getSpecificationsById(id);
    }

    @GetMapping("/category/{id}")
    public List<Product> getProductsByCategoryId(
            @PathVariable("id") long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String searchString
    ) {
        return productService.findByCategoryId(
            id,
            Optional.ofNullable(page),
            Optional.ofNullable(size),
            Optional.ofNullable(sortBy),
            Optional.ofNullable(searchString)
        );
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshProducts() {
       boolean updated = productService.updateProducts();
       return updated ? ResponseEntity.ok().build() : ResponseEntity.internalServerError().build();
    }
}
