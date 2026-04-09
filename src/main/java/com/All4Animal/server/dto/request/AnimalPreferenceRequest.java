package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.UserPreference;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AnimalPreferenceRequest {
    private UserPreference.PreferredAnimalType animalType;

    private UserPreference.PreferredSize size;

    private UserPreference.PreferredGender gender;

    private UserPreference.PreferredAgeGroup age;

    private UserPreference.PreferredNeuteredStatus status;

    private String personalities;

}
