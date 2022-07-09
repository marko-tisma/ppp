package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Category;
import com.mtisma.ppp.model.Image;
import com.mtisma.ppp.model.Product;
import com.mtisma.ppp.model.Specification;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class EponudaScraper implements ProductScraper {

    private static final String BASE_URL = "https://eponuda.com";

    public Collection<Product> scrape() {
        try {
            Document categoriesPage = fetchPage(BASE_URL + "/racunari/racunarske-komponente/");
            return categoriesPage.select(".b-object__title").parallelStream()
                .flatMap(e -> scrapeCategory(
                    new Category(e.select("span").text()),
                    e.select("a").attr("abs:href"),
                    1).stream()
                )
                .collect(toList());
        } catch (IOException e) {
            log.warn("Failed fetching categories page", e);
            return new ArrayList<>();
        }
    }

    private Document fetchPage(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    private List<Product> scrapeCategory(Category category, String url, int page) {
        Document productPage;
        try {
            productPage = fetchPage(url + "/" + page);
        } catch (IOException e) {
            log.warn("Failed fetching category: %s page: %d from: %s".formatted(category.getName(), page, url));
            return new ArrayList<>();
        }
        List<Product> products = productPage.select(".prWrap").parallelStream()
            .map(e -> {
                var product = scrapeProduct(e);
                product.setCategory(category);
                return product;
            })
            .collect(Collectors.toList());

        Element nextPageLink = productPage.select(".paginationControl > a").last();
        if (nextPageLink != null) {
            var path = nextPageLink.attr("href");
            try {
                int nextPage = Integer.parseInt(path.substring(path.lastIndexOf("/") + 1));
                if (nextPage > page) {
                    products.addAll(scrapeCategory(category, url, nextPage));
                }
            } catch (NumberFormatException ignored) {
            }
        }
        return products;
    }

    private Product scrapeProduct(Element productElement) {
        var product = new Product();
        var title = productElement.select(".b-paging-product__title > a");
        product.setName(title.text());
        String[] splitHref = title.attr("href").split("-");
        Long id = Long.parseLong(splitHref[splitHref.length - 1]);
        product.setId(id);

        String priceText = productElement.select(".b-paging-product__price").text();
        priceText = priceText.split("-")[0]
            .split(",")[0]
            .replace(".", "")
            .replace("din", "")
            .trim();
        BigDecimal price = BigDecimal.valueOf(Long.parseLong(priceText));
        product.setCurrentPrice(price);

        String imageSrc = productElement.select(".b-paging-product__media img").attr("src");
        String imageName = String.valueOf(Path.of(imageSrc).getFileName());
        try {
            byte[] imageData = fetchImageData(imageSrc);
            var image = new Image();
            image.setName(imageName);
            image.setData(imageData);
            product.setImages(new ArrayList<>(List.of(image)));
        } catch (IOException e) {
            log.warn("Failed fatching image data for product: %s from: %s".formatted(product.getName(), imageSrc));
        }

        var detailsUrl = BASE_URL + "/" + URLEncoder.encode(
            title.attr("href").substring(1), StandardCharsets.UTF_8
        );
        try {
            Document detailsPage = fetchPage(detailsUrl);
            scrapeProductDetails(product, detailsPage);
        } catch (Exception e) {
            log.warn("Failed fetching details for product: %s from: %s".formatted(product.getName(), detailsUrl));
        }
        return product;
    }

    private byte[] fetchImageData(String url) throws IOException {
        Connection.Response res = Jsoup.connect(url)
            .ignoreContentType(true)
            .execute();
        return res.bodyAsBytes();
    }

    private Product scrapeProductDetails(Product product, Document detailsPage) {
        List<Specification> specs = detailsPage.select("#SpecCont > ul > li").stream()
            .map(e -> Specification.builder()
                .product(product)
                .name(e.select(".lef").text())
                .value(e.select(".rig").text())
                .build()
            )
            .toList();
        product.setSpecifications(specs);
        Element description = detailsPage.select(".productDescription > ul > li").first();
        product.setDescription(description != null ? description.text() : null);
        Long id = Long.parseLong(detailsPage.select("#PRid").attr("value"));
        product.setId(id);
        return product;
    }
}
