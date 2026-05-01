package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.AnimalPreferenceRequest;
import com.All4Animal.server.dto.response.AnimalPreferenceResponse;
import com.All4Animal.server.entity.UserPreference;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.UserPreferenceRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnimalPreferenceResponse patchUserPreferences(Long userId, AnimalPreferenceRequest request) {
        validatePatchRequest(request);

        Users user = getUser(userId);

        UserPreference preference = userPreferenceRepository.findByUserUserId(userId)
                .map(existingPreference -> updatePreference(existingPreference, request))
                .orElseGet(() -> createPreference(user, request));

        UserPreference savedPreference = userPreferenceRepository.save(preference);
        user.setUserPreference(savedPreference);

        return AnimalPreferenceResponse.toResponse(savedPreference);
    }

    private Users getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private void validatePatchRequest(AnimalPreferenceRequest request) {
        if (!hasAnyField(request)) {
            throw new IllegalArgumentException("최소 1개 이상의 선호 항목을 요청해야 합니다.");
        }
    }

    private UserPreference createPreference(Users user, AnimalPreferenceRequest request) {
        validateCreateRequest(request);

        return UserPreference.builder()
                .user(user)
                .preferredAnimalType(request.getAnimalType())
                .preferredSize(request.getSize())
                .preferredGender(request.getGender())
                .preferredAgeGroup(request.getAge())
                .preferredNeuteredStatus(request.getStatus())
                .preferredPersonality(request.getPersonalities())
                .build();
    }

    private UserPreference updatePreference(UserPreference preference, AnimalPreferenceRequest request) {
        if (request.getAnimalType() != null) {
            preference.setPreferredAnimalType(request.getAnimalType());
        }
        if (request.getSize() != null) {
            preference.setPreferredSize(request.getSize());
        }
        if (request.getGender() != null) {
            preference.setPreferredGender(request.getGender());
        }
        if (request.getAge() != null) {
            preference.setPreferredAgeGroup(request.getAge());
        }
        if (request.getStatus() != null) {
            preference.setPreferredNeuteredStatus(request.getStatus());
        }
        if (request.getPersonalities() != null) {
            preference.setPreferredPersonality(request.getPersonalities());
        }

        return preference;
    }

    private void validateCreateRequest(AnimalPreferenceRequest request) {
        if (!hasAllFieldsForCreate(request)) {
            throw new IllegalArgumentException("선호 정보가 없는 사용자는 모든 선호 항목을 포함해 요청해야 생성할 수 있습니다.");
        }
    }

    private boolean hasAnyField(AnimalPreferenceRequest request) {
        return request.getAnimalType() != null
                || request.getSize() != null
                || request.getGender() != null
                || request.getAge() != null
                || request.getStatus() != null
                || StringUtils.hasText(request.getPersonalities());
    }

    private boolean hasAllFieldsForCreate(AnimalPreferenceRequest request) {
        return request.getAnimalType() != null
                && request.getSize() != null
                && request.getGender() != null
                && request.getAge() != null
                && request.getStatus() != null
                && StringUtils.hasText(request.getPersonalities());
    }

}