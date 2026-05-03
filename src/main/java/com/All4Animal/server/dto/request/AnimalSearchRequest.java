package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.Animal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimalSearchRequest {
    private String keyword;

    private String careAddr;

    private Animal.AnimalType animalType;
}
