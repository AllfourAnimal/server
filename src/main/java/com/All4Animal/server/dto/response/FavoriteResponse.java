package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.AnimalImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FavoriteResponse {
    public enum Gender{
        MALE,
        FEMALE,
        NEUTERED
    }

    private String speices;
    private List<AnimalImage> images;
    private Long animalAge;
    private Gender animlSex;
    private String animalStory;
}
