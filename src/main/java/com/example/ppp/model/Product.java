package com.example.ppp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Category category;

    @Column(unique = true)
    private String name;

    private BigDecimal currentPrice;

    @Lob
    private String description;

    @OneToMany(mappedBy = "product")
    private List<Specification> specifications;

}
