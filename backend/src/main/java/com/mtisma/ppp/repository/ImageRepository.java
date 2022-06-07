package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

    Optional<Image> findByName(String name);
}