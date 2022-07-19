package com.mtisma.ppp.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Product product;

    @Column(unique = true)
    @NotBlank
    private String name;

    @JsonIgnore
    @NotBlank
    private String location;

    @Transient
    private byte[] data;
}
