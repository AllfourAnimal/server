package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.UserProfile;
import com.All4Animal.server.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;
    private String loginId;
    private String username;
    private Long profileId;
    private UserProfile.HousingType housingType;
    private Boolean hasChildren;
    private Boolean hasElderly;
    private Boolean hasOtherPets;
    private Integer absenceHours;
    private UserProfile.ActivityLevel activityLevel;
    private UserProfile.PetExperience petExperience;
    private UserProfile.PreferredSpecies preferredSpecies;
    private UserProfile.PreferredSize preferredSize;
    private UserProfile.PreferredAgeGroup preferredAgeGroup;
    private UserProfile.NoiseTolerance noiseTolerance;
    private String allergyInfo;
    private String specialNote;
    private Boolean profileCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserProfileResponse from(Users user, UserProfile profile) {
        return new UserProfileResponse(
                user.getUserId(),
                user.getLoginId(),
                user.getUsername(),
                profile.getProfileId(),
                profile.getHousingType(),
                profile.getHasChildren(),
                profile.getHasElderly(),
                profile.getHasOtherPets(),
                profile.getAbsenceHours(),
                profile.getActivityLevel(),
                profile.getPetExperience(),
                profile.getPreferredSpecies(),
                profile.getPreferredSize(),
                profile.getPreferredAgeGroup(),
                profile.getNoiseTolerance(),
                profile.getAllergyInfo(),
                profile.getSpecialNote(),
                profile.getProfileCompleted(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
