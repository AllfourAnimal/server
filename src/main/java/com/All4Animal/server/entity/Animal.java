package com.All4Animal.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
    @Column(name = "animal_id")
    private Long animal_id;

    private double weight;

    private Integer animal_age;

    private String persona;

    private Gender animal_sex;

    private boolean isVaccinated;

    private String description;

    private boolean isAdopted;

    private LocalDateTime createdAt;

    private String happenPlace;

    private String careNm;

    private String careTel;

    private String careAddr;
}
