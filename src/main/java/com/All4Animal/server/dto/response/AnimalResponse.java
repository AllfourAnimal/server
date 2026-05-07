package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Animal;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval people_friendly;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval active_playful;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval calm_quiet;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval adaptable;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval outdoor_activity;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval animal_friendly;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval beginner_possible;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval family_friendly;

    @Enumerated(EnumType.STRING)
    private Animal.ScoreInterval slow_bonding_ok;


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
                .people_friendly(animal.getPeople_friendly())
                .active_playful(animal.getActive_playful())
                .calm_quiet(animal.getCalm_quiet())
                .adaptable(animal.getAdaptable())
                .outdoor_activity(animal.getOutdoor_activity())
                .animal_friendly(animal.getAnimal_friendly())
                .beginner_possible(animal.getBeginner_possible())
                .family_friendly(animal.getFamily_friendly())
                .slow_bonding_ok(animal.getSlow_bonding_ok())
                .build();
    }
}