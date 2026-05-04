package com.All4Animal.server.dto.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeoulAnimalApiResponse {
    @JsonProperty("SEQ")
    private String seq;

    @JsonProperty("ANIMAL_NM")
    private String animalNm;

    @JsonProperty("ANIMAL_TYPE")
    private String animalType;

    @JsonProperty("ANIMAL_BREED")
    private String animalBreed;

    @JsonProperty("ANIMAL_SEX")
    private String animalSex;

    @JsonProperty("ANIMAL_BRITH_YMD")
    private String animalBirthYmd;

    @JsonProperty("WEIGHT_KG")
    private Double weightKg;

    @JsonProperty("ADMISSION_DT")
    private String admissionDt;

    @JsonProperty("ADOPT_STATUS")
    private String adoptStatus;

    @JsonProperty("FOSTER_STATUS")
    private String fosterStatus;

    @JsonProperty("MOVIE_URL")
    private String movieUrl;

    @JsonProperty("CONT")
    private String cont;
}