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

    private Long animal_id;

//    private List<AnimalImage> images;
    private String thumbnailImageUrl;

    private Long animal_age;

    private Gender animl_sex;

    private String animalStory;
}
