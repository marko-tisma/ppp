package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SpecificationRepository extends CrudRepository<Specification, Long> {
    List<Specification> findByProductId(Long id);
    Optional<Specification> findByProductIdAndName(Long id, String name);
}
