package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.model.Specification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ScraperTests {

    private final EponudaScraper scraper = new EponudaScraper();
    private Collection<Product> products = new ArrayList<>();

    @BeforeAll
    public void scrape() {
        products = scraper.scrape();
    }

    @Test
    public void testScraping() {
        assertTrue(products.size() > 0);
        assertTrue(products.stream()
                .noneMatch(p -> p.getName().isEmpty())
        );
    }

    @Test
    public void testCategoryScraping() {
        Set<String> categoryNames = Set.of(
                "Procesori", "Matične ploče", "Grafičke kartice", "Hard diskovi", "Ram memorija", "Napajanja",
                "Kućišta za računare", "Kuleri", "Network attached storage (NAS)", "Oprema za hladjenje"
        );
        assertTrue(products.stream()
                .allMatch(p -> categoryNames.contains(p.getCategory().getName()))
        );
    }

    @Test
    public void testProductDetailsScraping() {
        String testProductName = "Intel Core i3-9100F 3.6GHz BOX LGA1151 procesor";
        List<Product> result = products.stream()
                .filter(p -> p.getName().equals(testProductName))
                .toList();
        assertEquals(1, result.size());

        Product testProduct = result.get(0);
        assertEquals("Procesori", testProduct.getCategory().getName());
        Map<String, String> expectedSpecs = Map.of(
                "Podnožje", "Socket 1151",
                "Brzina procesora", "3.6 GHz",
                "Proces proizvodnje", "14 nm",
                "Vrsta procesora", "Intel Core i3",
                "Broj jezgra", "4",
                "Brend", "Intel"
        );
        assertEquals(expectedSpecs, testProduct.getSpecifications().stream()
                .collect(Collectors.toMap(
                        Specification::getName,
                        Specification::getValue)
                ));
        assertFalse(testProduct.getDescription().isEmpty());
        assertEquals(1, testProduct.getImages().size());
    }
}
