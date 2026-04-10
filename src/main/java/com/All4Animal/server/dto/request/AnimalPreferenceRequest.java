package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.UserPreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AnimalPreferenceRequest {
    @Schema(description = "선호 동물 종류", example = "DOG")
    private UserPreference.PreferredAnimalType animalType;

    @Schema(description = "선호 크기", example = "MEDIUM")
    private UserPreference.PreferredSize size;

    @Schema(description = "선호 성별", example = "FEMALE")
    private UserPreference.PreferredGender gender;

    @Schema(description = "선호 연령대", example = "ADULT")
    private UserPreference.PreferredAgeGroup age;

    @Schema(description = "중성화 여부", example = "YES")
    private UserPreference.PreferredNeuteredStatus status;

    @Schema(description = "선호 성격", example = "활발함, 사람을 좋아함")
    private String personalities;

}
