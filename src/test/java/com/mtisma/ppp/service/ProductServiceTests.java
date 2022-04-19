package com.mtisma.ppp.service;


import com.mtisma.ppp.model.Category;
import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.repository.ImageFileRepository;
import com.mtisma.ppp.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTests {

    @MockBean(name = "eponudaScraper")
    private ProductScraper productScraperMock;

    @MockBean
    private ImageFileRepository fileRepositoryMock;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        given(fileRepositoryMock.save(anyString(), any(byte[].class))).willReturn(Optional.empty());
    }

    @Test
    @Transactional
    public void testSameProductCategoryUpdate() {
        Category mockCategory = new Category("Procesori");
        Product mockProduct1 = Product.builder()
                .id(1L)
                .name("Intel Core i2-9100F 3.6GHz BOX LGA1151 procesor")
                .category(mockCategory)
                .build();
        Product mockProduct2 = Product.builder()
                .id(2L)
                .name("mock2")
                .category(mockCategory)
                .build();
        Product mockProduct3 = Product.builder()
                .id(3L)
                .name("mock3")
                .category(new Category("Procesori"))
                .build();

        Set<Product> productsMock = Set.of(mockProduct1, mockProduct2, mockProduct3);
        given(productScraperMock.scrape()).willReturn(productsMock);
        productService.updateProducts();
        assertSame(3L, productRepository.count());
        productsMock.forEach(p -> {
            Optional<Product> res = productRepository.findOneByName(p.getName());
            assertTrue(res.isPresent());
            assertSame(res.get().getName(), p.getName());
            assertSame(res.get().getCategory().getName(), p.getCategory().getName());
            assertSame(res.get().getCategory().getId(), p.getCategory().getId());
        });
    }

    @Test
    @Transactional
    public void testSameProductIdUpdate() {
        Category mockCategory = new Category("Procesori");
        Product mockProduct1 = Product.builder()
                .id(1L)
                .name("Intel Core i2-9100F 3.6GHz BOX LGA1151 procesor")
                .category(mockCategory)
                .build();
        Product mockProduct2 = Product.builder()
                .id(1L)
                .name("mock")
                .category(mockCategory)
                .build();

        List<Product> productsMock = List.of(mockProduct1, mockProduct2);
        given(productScraperMock.scrape()).willReturn(productsMock);
        productService.updateProducts();
        assertSame(1L, productRepository.count());
        assertTrue(productRepository.findOneByName("mock").isPresent());
        assertSame(1L, productRepository.findOneByName("mock").get().getId());
    }

}