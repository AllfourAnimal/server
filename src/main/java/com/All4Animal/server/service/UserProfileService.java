package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.UserProfileRequest;
import com.All4Animal.server.dto.response.UserProfileResponse;
import com.All4Animal.server.entity.UserProfile;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.exception.ProfileNotFoundException;
import com.All4Animal.server.repository.UserProfileRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(String loginId) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ProfileNotFoundException("사용자를 찾을 수 없습니다."));

        UserProfile profile = userProfileRepository.findByUsersLoginId(loginId)
                .orElseThrow(() -> new ProfileNotFoundException("사용자 프로필을 찾을 수 없습니다."));

        return UserProfileResponse.from(user, profile);
    }

    @Transactional
    public UserProfileResponse saveMyProfile(String loginId, UserProfileRequest request) {
        Users user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new ProfileNotFoundException("사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();

        UserProfile profile = userProfileRepository.findByUsersLoginId(loginId)
                .orElse(
                        UserProfile.builder()
                                .users(user)
                                .createdAt(now)
                                .build()
                );

//        profile.setUsers(user);
//        profile.setHousingType(request.getHousingType());
//        profile.setHasChildren(request.getHasChildren());
//        profile.setHasElderly(request.getHasElderly());
//        profile.setHasOtherPets(request.getHasOtherPets());
//        profile.setAbsenceHours(request.getAbsenceHours());
//        profile.setActivityLevel(request.getActivityLevel());
//        profile.setPetExperience(request.getPetExperience());
//        profile.setPreferredSpecies(request.getPreferredSpecies());
//        profile.setPreferredSize(request.getPreferredSize());
//        profile.setPreferredAgeGroup(request.getPreferredAgeGroup());
//        profile.setNoiseTolerance(request.getNoiseTolerance());
//        profile.setAllergyInfo(request.getAllergyInfo());
//        profile.setSpecialNote(request.getSpecialNote());
//        profile.setProfileCompleted(true);
//        profile.setUpdatedAt(now);

        profile.updateProfile(user, request, now);

        if (profile.getCreatedAt() == null) {
            profile.setCreatedAt(now);
        }

        UserProfile savedProfile = userProfileRepository.save(profile);
        user.setUserProfile(savedProfile);

        return UserProfileResponse.from(user, savedProfile);
    }
}
