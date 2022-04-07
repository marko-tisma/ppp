package com.example.ppp.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Specification {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Product product;

    private String name;

    private String value;

}
