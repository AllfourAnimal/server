package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Animal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnimalResponse {

    private Long animalId;
    private String desertionNo;
    private Animal.AnimalType animalType;
    private String species;
    private double weight;
    private Integer animal_age;
    private String persona;
    private Animal.Gender animal_sex;
    private boolean isVaccinated;
    private String description;
    private boolean isAdopted;
    private LocalDateTime createdAt;
    private String happenPlace;
    private String careNm;
    private String careTel;
    private String careAddr;

    public static AnimalResponse from(Animal animal) {
        return AnimalResponse.builder()
                .animalId(animal.getAnimalId())
                .desertionNo(animal.getDesertionNo())
                .animalType(animal.getAnimalType())
                .species(animal.getSpecies())
                .weight(animal.getWeight())
                .animal_age(animal.getAnimal_age())
                .persona(animal.getPersona())
                .animal_sex(animal.getAnimal_sex())
                .isVaccinated(animal.isVaccinated())
                .description(animal.getDescription())
                .isAdopted(animal.isAdopted())
                .createdAt(animal.getCreatedAt())
                .happenPlace(animal.getHappenPlace())
                .careNm(animal.getCareNm())
                .careTel(animal.getCareTel())
                .careAddr(animal.getCareAddr())
                .build();
    }
}