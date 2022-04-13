package com.mtisma.ppp.service;

import com.mtisma.ppp.model.Product;

import java.util.Collection;

public interface ProductScraper {

    Collection<Product> scrape();
}
