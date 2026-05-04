package com.All4Animal.server.dto.response.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeoulAnimalImageApiResponse {

    @JsonProperty("SEQ")
    private String seq;

    @JsonProperty("IMG_TYPE")
    private String imgType;

    @JsonProperty("IMG_NUM")
    private Integer imgNum;

    @JsonProperty("IMG_URL")
    private String imgUrl;
}