package com.mtisma.ppp.model;

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
    private Product product;

    @Column(unique = true)
    @NotNull
    private String name;

    private String location;

    @Transient
    private byte[] data;
}
