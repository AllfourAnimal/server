package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.UserPreference;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class AnimalPreferenceResponse {
    private String animalType;
    private String size;
    private String gender;
    private String age;
    private String personalities;
    private LocalDateTime updatedAt;

    public static AnimalPreferenceResponse toResponse(UserPreference preference) {
        return new AnimalPreferenceResponse(
                enumName(preference.getPreferredAnimalType()),
                enumName(preference.getPreferredSize()),
                enumName(preference.getPreferredGender()),
                enumName(preference.getPreferredAgeGroup()),
                preference.getPreferredPersonality(),
                LocalDateTime.now()
        );
    }

    private static String enumName(Enum<?> enumValue) {
        return Objects.nonNull(enumValue) ? enumValue.name() : null;
    }
}
