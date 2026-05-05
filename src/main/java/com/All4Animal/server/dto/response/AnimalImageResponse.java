package com.All4Animal.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;



@Getter
@AllArgsConstructor
public class AnimalImageResponse {

    private String imageUrl;

    @JsonProperty("is_ai_image")
    private boolean aiImage;
}
