package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Category;
import com.mtisma.ppp.model.Image;
import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.repository.CategoryRepository;
import com.mtisma.ppp.repository.ImageFileRepository;
import com.mtisma.ppp.repository.ImageRepository;
import com.mtisma.ppp.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final ProductScraper scraper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ImageFileRepository fileRepository;

    public ProductService(ProductScraper scraper,
                          ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          ImageRepository imageRepository,
                          ImageFileRepository fileRepository) {
        this.scraper = scraper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.imageRepository = imageRepository;
        this.fileRepository = fileRepository;
    }

    public List<Product> findAll() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
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

    public Optional<Product> findById(long id) {
        return productRepository.findById(id);
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

        categories.entrySet().parallelStream()
                .forEach(c -> {
                    Category category = categoryRepository.findOneByName(c.getKey().getName())
                            .orElseGet(() -> categoryRepository.save(c.getKey()));
                    for (Product product : c.getValue()) {
                        product.setCategory(category);
                        productRepository.save(product);

                        if (product.getImages() == null || product.getImages().size() == 0) {
                            continue;
                        }
                        Image image = product.getImages().get(0);
                        Optional<FileSystemResource> imageResource = fileRepository.getFile(image.getName());
                        if (imageResource.isPresent()) {
                            product.getImages().remove(0);
                            continue;
                        }
                        Optional<String> location = fileRepository.save(image.getName(), image.getData());
                        if (location.isPresent()) {
                            image.setProduct(product);
                            image.setLocation(location.get());
                            imageRepository.save(image);
                            log.info("Saved image: %s of product: %s at location: %s"
                                    .formatted(image.getName(), product.getName(), location));
                        } else {
                            product.getImages().remove(0);
                            log.error("Failed to save image: %s of product: %s at location: %s"
                                    .formatted(image.getName(), product.getName(), location));
                        }
                    }
        });
        return true;
    }

}
