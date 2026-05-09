package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.ProfileUpdateRequest;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.UserProfileResponse;
import com.All4Animal.server.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "프로필 API")
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "내 프로필 조회", description = "Authorization 토큰을 기준으로 현재 로그인한 사용자의 프로필 정보를 조회합니다. userId와 password는 반환하지 않습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "loginId": "all4animal",
                                              "username": "홍길동",
                                              "phone": "01012345678",
                                              "birthYear": 1998,
                                              "location": "서울 마포구",
                                              "isExperience": true,
                                              "housingType": "APARTMENT_VILLA",
                                              "emptyTime": 6,
                                              "createdAt": "2026-05-09T12:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "UNAUTHORIZED",
                                              "message": "인증된 사용자 정보를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<UserProfileResponse> getMyProfile() {
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @Operation(summary = "내 프로필 수정", description = "Authorization 토큰을 기준으로 현재 로그인한 사용자의 프로필 정보를 수정합니다. 요청에 없는 필드는 기존 값을 유지합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "프로필 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserProfileResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "loginId": "all4animal",
                                              "username": "김길동",
                                              "phone": "01098765432",
                                              "birthYear": 1998,
                                              "location": "서울 마포구",
                                              "isExperience": true,
                                              "housingType": "APARTMENT_VILLA",
                                              "emptyTime": 8,
                                              "createdAt": "2026-05-09T12:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "요청값 검증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "code": "VALIDATION_ERROR",
                                              "message": "요청값이 올바르지 않습니다.",
                                              "errors": {
                                                "phone": "전화번호는 숫자 10~11자리여야 합니다."
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PatchMapping
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequest request
    ) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }
}
