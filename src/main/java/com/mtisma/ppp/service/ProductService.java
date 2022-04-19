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
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public boolean updateProducts() {
        log.info("Products update started");
        Collection<Product> products = scraper.scrape();
        log.info("Scraped: " + products.size() + " products");
        if (products.size() == 0) {
            return false;
        }

        Map<Category, List<Product>> categories = products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        categories.forEach((c, p) -> {
            Category category = categoryRepository.findOneByName(c.getName())
                    .orElseGet(() -> categoryRepository.save(c));
            for (Product product : p) {
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
                location.ifPresentOrElse(l -> {
                    image.setLocation(l);
                    imageRepository.save(image);
                    log.info("Saved image: %s of product: %s at location: %s"
                            .formatted(image.getName(), product.getName(), location));
                }, () -> product.getImages().remove(0));
            }
        });
        return true;
    }

}
