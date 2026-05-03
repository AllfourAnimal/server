package com.All4Animal.server.dto.response;


import com.All4Animal.server.entity.Animal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnimalFilterResponse {

    private Long animalId;

    private Animal.AnimalType animalType;

    private String species;

    private String description;

    private String happenPlace;

    private String careNm;

    private String careAddr;

    public static AnimalFilterResponse from(Animal animal) {
        return new AnimalFilterResponse(
                animal.getAnimalId(),
                animal.getAnimalType(),
                animal.getSpecies(),
                animal.getDescription(),
                animal.getHappenPlace(),
                animal.getCareNm(),
                animal.getCareAddr()
        );
    }
}
