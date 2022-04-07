package com.example.ppp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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
    private String name;

    private String location;

}
