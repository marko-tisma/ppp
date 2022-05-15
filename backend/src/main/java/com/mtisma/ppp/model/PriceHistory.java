package com.mtisma.ppp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PriceHistory {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Product product;

    private BigDecimal amount;

    private LocalDateTime at;

}
