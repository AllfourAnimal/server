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

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnimalPreferenceResponse putUserPreferences(Long userId, AnimalPreferenceRequest request) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserPreference userPreference = userPreferenceRepository.findByUserUserId(userId)
                .orElseGet(() -> UserPreference.builder().user(user).build());

        userPreference.setUser(user);
        userPreference.setPreferredAnimalType(request.getAnimalType());
        userPreference.setPreferredSize(request.getSize());
        userPreference.setPreferredGender(request.getGender());
        userPreference.setPreferredAgeGroup(request.getAge());
        userPreference.setPreferredPersonality(request.getPersonalities());
        userPreference.setPreferredNeuteredStatus(request.getStatus());

        UserPreference savedPreference = userPreferenceRepository.save(userPreference);
        user.setUserPreference(savedPreference);

        return new AnimalPreferenceResponse(
                savedPreference.getPreferredAnimalType().name(),
                savedPreference.getPreferredSize().name(),
                savedPreference.getPreferredGender().name(),
                savedPreference.getPreferredAgeGroup().name(),
                savedPreference.getPreferredPersonality(),
                true,
                LocalDateTime.now()
        );
    }
}
