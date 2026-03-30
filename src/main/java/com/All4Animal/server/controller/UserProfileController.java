package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.UserProfileRequest;
import com.All4Animal.server.dto.response.UserProfileResponse;
import com.All4Animal.server.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Profile", description = "사용자 프로필 API")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Operation(summary = "내 프로필 조회", description = "JWT 토큰 기준으로 현재 로그인한 사용자의 프로필을 조회합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(userProfileService.getMyProfile(loginId));
    }

    @Operation(summary = "내 프로필 저장", description = "JWT 토큰 기준으로 현재 로그인한 사용자의 프로필 정보를 저장하거나 수정합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/profile")
    public ResponseEntity<UserProfileResponse> saveMyProfile(
            Authentication authentication,
            @Valid @RequestBody UserProfileRequest request
    ) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(userProfileService.saveMyProfile(loginId, request));
    }
}
