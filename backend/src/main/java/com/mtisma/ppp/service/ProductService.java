package com.mtisma.ppp.service;

import com.mtisma.ppp.model.*;
import com.mtisma.ppp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductScraper scraper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final SpecificationRepository specificationRepository;
    private final ImageRepository imageRepository;
    private final ImageFileRepository fileRepository;

    public ProductService(ProductScraper scraper,
                          ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          PriceHistoryRepository priceHistoryRepository,
                          SpecificationRepository specificationRepository,
                          ImageRepository imageRepository,
                          ImageFileRepository fileRepository) {
        this.scraper = scraper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.specificationRepository = specificationRepository;
        this.imageRepository = imageRepository;
        this.fileRepository = fileRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByCategoryId(
            long id,
            Optional<Integer> page,
            Optional<Integer> size,
            Optional<String> sortBy,
            Optional<String> nameQuery
    ) {
        PageRequest pageable = PageRequest.of(page.orElse(0), size.orElse(Integer.MAX_VALUE));
        if (sortBy.isPresent()) {
            String property = sortBy.get();
            Sort.Direction direction = Sort.Direction.ASC;
            if (property.charAt(0) == '+') {
                property = property.substring(1);
            } else if (property.charAt(0) == '-') {
                direction = Sort.Direction.DESC;
                property = property.substring(1);
            }

            pageable = pageable.withSort(Sort.by(direction, property));
        }
        return productRepository.findByCategoryIdAndNameContaining(id, nameQuery.orElse(""), pageable);
    }

    @Scheduled(cron = "0 0 15 * * *")
    public void dailyRefresh() {
        updateProducts();
    }

    public boolean updateProducts() {
        log.info("Products update started");
        Collection<Product> products = scraper.scrape();
        log.info("Scraped: " + products.size() + " products");
        if (products.size() == 0) {
            return false;
        }

        Map<Category, List<Product>> categories = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        var now = LocalDate.now();
        categories.entrySet().parallelStream()
                .forEach(c -> {
                    Category category = categoryRepository.findOneByName(c.getKey().getName())
                            .orElseGet(() -> categoryRepository.save(c.getKey()));
                    for (Product product : c.getValue()) {
                        product.setCategory(category);
                        saveProduct(product, now);
                    }
        });
        return true;
    }

    @Transactional
    public void saveProduct(Product product, LocalDate now) {
        productRepository.save(product);
        priceHistoryRepository.save(PriceHistory.builder()
                .product(product)
                .amount(product.getCurrentPrice())
                .createdAt(now)
                .build()
        );
        if (product.getSpecifications() != null) {
            product.getSpecifications().forEach(s -> {
                Specification spec = specificationRepository.findByProductIdAndName(product.getId(), s.getName()).orElse(s);
                spec.setValue(s.getValue());
                specificationRepository.save(spec);
            });
        }
        if (product.getImages() == null || product.getImages().size() == 0) {
            return;
        }
        Image thumbnail = product.getImages().get(0);
        if (imageRepository.findByName(thumbnail.getName()).isPresent()) {
            return;
        }
        Optional<String> location = fileRepository.save(thumbnail.getName(), thumbnail.getData());
        if (location.isPresent()) {
            thumbnail.setProduct(product);
            thumbnail.setLocation(location.get());
            imageRepository.save(thumbnail);
            log.info("Saved thumbnail: %s of product: %s at location: %s"
                    .formatted(thumbnail.getName(), product.getName(), location));
        } else {
            log.error("Failed to save thumbnail: %s of product: %s at location: %s"
                    .formatted(thumbnail.getName(), product.getName(), location));
        }
    }

    public List<PriceHistory> getProductHistory(Long productId, Optional<LocalDate> from, Optional<LocalDate> to) {
        return priceHistoryRepository.findByProductIdAndCreatedAtBetween(
                productId, from.orElse(LocalDate.MIN), to.orElse(LocalDate.MAX)
        );
    }

    public List<Specification> getSpecificationsById(long id) {
        return specificationRepository.findByProductId(id);
    }
}
