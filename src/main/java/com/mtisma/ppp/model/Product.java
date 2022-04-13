package com.mtisma.ppp.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    private Long id;

    @ManyToOne
    private Category category;

    @NotNull
    private String name;

    private BigDecimal currentPrice;

    @Lob
    private String description;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Specification> specifications;

    @OneToMany(mappedBy = "product")
    private List<Image> images;
}
