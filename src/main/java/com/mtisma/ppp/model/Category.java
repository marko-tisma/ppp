package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Category {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    @NotNull
    private String name;

    @OneToMany(mappedBy = "category")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    public Category(String name) { this.name = name; }
}
