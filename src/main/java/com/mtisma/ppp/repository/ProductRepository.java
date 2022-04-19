package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    Optional<Product> findOneByName(String name);
}
