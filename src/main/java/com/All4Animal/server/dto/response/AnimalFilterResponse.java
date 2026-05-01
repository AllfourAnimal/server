package com.All4Animal.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AnimalFilterResponse {
    @Schema(description = "추천된 동물 수", example = "3")
    private Integer count;

    @Schema(description = "추천된 동물 목록")
    private List<RecommendedAnimalResponse> animals;
}
