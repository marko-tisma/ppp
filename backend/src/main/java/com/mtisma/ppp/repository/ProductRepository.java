package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    List<Product> findAll();
    Optional<Product> findOneByName(String name);
    List<Product> findByCategoryIdAndNameContaining(long id, String nameQuery, Pageable pageable);
}
