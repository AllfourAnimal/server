package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.ProfileUpdateRequest;
import com.All4Animal.server.dto.response.UserProfileResponse;
import com.All4Animal.server.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final AuthService authService;

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile() {
        Users user = authService.getCurrentUser();
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(ProfileUpdateRequest request) {
        Users user = authService.getCurrentUser();

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthYear() != null) {
            user.setBirthYear(request.getBirthYear());
        }
        if (request.getLocation() != null) {
            user.setLocation(request.getLocation());
        }
        if (request.getIsExperience() != null) {
            user.setExperience(request.getIsExperience());
        }
        if (request.getHousingType() != null) {
            user.setHousingType(request.getHousingType());
        }
        if (request.getEmptyTime() != null) {
            user.setEmptyTime(request.getEmptyTime());
        }

        return UserProfileResponse.from(user);
    }
}
