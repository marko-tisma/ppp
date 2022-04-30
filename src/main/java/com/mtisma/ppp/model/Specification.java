package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Specification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Product product;

    @NotNull
    private String name;

    private String value;
}
