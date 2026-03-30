package com.All4Animal.server.dto.request;

import com.All4Animal.server.entity.UserProfile;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileRequest {

    @NotNull(message = "주거 형태는 필수입니다.")
    private UserProfile.HousingType housingType;

    @NotNull(message = "자녀 여부는 필수입니다.")
    private Boolean hasChildren;

    @NotNull(message = "노인 동거 여부는 필수입니다.")
    private Boolean hasElderly;

    @NotNull(message = "다른 반려동물 여부는 필수입니다.")
    private Boolean hasOtherPets;

    @NotNull(message = "외출 시간은 필수입니다.")
    @Min(value = 0, message = "외출 시간은 0시간 이상이어야 합니다.")
    @Max(value = 24, message = "외출 시간은 24시간 이하여야 합니다.")
    private Integer absenceHours;

    @NotNull(message = "활동량은 필수입니다.")
    private UserProfile.ActivityLevel activityLevel;

    @NotNull(message = "반려동물 경험은 필수입니다.")
    private UserProfile.PetExperience petExperience;

    @NotNull(message = "선호 동물 종류는 필수입니다.")
    private UserProfile.PreferredSpecies preferredSpecies;

    @NotNull(message = "선호 크기는 필수입니다.")
    private UserProfile.PreferredSize preferredSize;

    @NotNull(message = "선호 연령대는 필수입니다.")
    private UserProfile.PreferredAgeGroup preferredAgeGroup;

    private UserProfile.NoiseTolerance noiseTolerance;

    @Size(max = 255, message = "알레르기 정보는 255자 이하여야 합니다.")
    private String allergyInfo;

    @Size(max = 500, message = "특이사항은 500자 이하여야 합니다.")
    private String specialNote;
}
