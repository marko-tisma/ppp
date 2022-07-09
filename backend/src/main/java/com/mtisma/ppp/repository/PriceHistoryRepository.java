package com.mtisma.ppp.repository;

import com.mtisma.ppp.model.PriceHistory;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryRepository extends CrudRepository<PriceHistory, Long> {

    List<PriceHistory> findByProductIdAndCreatedAtBetween(Long productId, LocalDate from, LocalDate to);
}
