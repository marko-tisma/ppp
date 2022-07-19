package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    private String name;

    private BigDecimal currentPrice;

    @Lob
    private String description;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Specification> specifications;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<Image> images;
}
