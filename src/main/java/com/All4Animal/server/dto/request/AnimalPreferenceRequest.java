package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.UserPreference;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AnimalPreferenceRequest {
    @NotNull
    private UserPreference.PreferredAnimalType animalType;

    @NotNull
    private UserPreference.PreferredSize size;

    @NotNull
    private UserPreference.PreferredGender gender;

    @NotNull
    private UserPreference.PreferredAgeGroup age;

    private UserPreference.PreferredNeuteredStatus status;

    @NotEmpty
    private String personalities;

}
