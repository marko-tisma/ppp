package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    List<Category> findAll();
    Optional<Category> findOneByName(String name);
}
