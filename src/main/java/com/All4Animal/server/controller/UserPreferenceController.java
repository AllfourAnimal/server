package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.AnimalPreferenceRequest;
import com.All4Animal.server.dto.response.AnimalPreferenceResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.UserPreferenceService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Preference", description = "선호 API")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserPreferenceService userPreferenceService;
    private final AuthService authService;

    @PutMapping("/preferences")
    public ResponseEntity<AnimalPreferenceResponse> putUserPreferences(
            @Valid @RequestBody AnimalPreferenceRequest request
    ){
        Long userId = authService.getCurrentUserId();
        AnimalPreferenceResponse response = userPreferenceService.putUserPreferences(userId, request);
        return ResponseEntity.ok(response);
    }

}
