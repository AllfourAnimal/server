package com.All4Animal.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "Animal")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Animal {
    public enum Gender{
        MALE,
        FEMALE,
        NEUTERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long animalId;

    private String species;

    private double weight;

    private Integer animalAge;

    private String persona;

    private Gender animalSex;

    private boolean isVaccinated;

    private String description;

    private boolean isAdopted;

    private LocalDateTime createdAt;

    private String happenPlace;

    private String careNm;

    private String careTel;

    private String careAddr;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

}
