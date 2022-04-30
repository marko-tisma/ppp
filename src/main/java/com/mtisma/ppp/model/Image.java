package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
public class Image {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Product product;

    @Column(unique = true)
    @NotNull
    private String name;

    @JsonIgnore
    private String location;

    @Transient
    private byte[] data;
}
