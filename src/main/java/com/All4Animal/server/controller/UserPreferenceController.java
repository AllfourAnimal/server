package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.AnimalPreferenceRequest;
import com.All4Animal.server.dto.response.AnimalPreferenceResponse;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.UserPreferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @GetMapping("/preferences")
    public ResponseEntity<AnimalPreferenceResponse> getUserPreferences(){
        Long userId = authService.getCurrentUserId();
        AnimalPreferenceResponse response = userPreferenceService.getUserPreferences(userId);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "선호 정보 수정", description = "선호 정보가 있으면 요청에 포함된 필드만 수정하고, 없으면 전체 필드 요청 시 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "선호 정보 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalPreferenceResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "MEDIUM",
                      "gender": "FEMALE",
                      "age": "ADULT",
                      "personalities": "활발함, 사람을 좋아함",
                      "updatedAt": "2026-04-10T10:30:00"
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "empty_request",
                                            value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "최소 1개 이상의 선호 항목을 요청해야 합니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "create_requires_all_fields",
                                            value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "선호 정보가 없는 사용자는 모든 선호 항목을 포함해 요청해야 생성할 수 있습니다."
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "unauthorized",
                                    value = """
                    {
                      "code": "UNAUTHORIZED",
                      "message": "인증 정보가 유효하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AnimalPreferenceRequest.class),
                    examples = {
                            @ExampleObject(
                                    name = "animal_type_dog",
                                    summary = "선호 동물 종류: 강아지",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "MEDIUM",
                      "gender": "FEMALE",
                      "age": "ADULT",
                      "personalities": "활발함, 사람 좋아함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "animal_type_cat",
                                    summary = "선호 동물 종류: 고양이",
                                    value = """
                    {
                      "animalType": "CAT",
                      "size": "SMALL",
                      "gender": "MALE",
                      "age": "YOUNG",
                      "personalities": "호기심 많음"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "animal_type_any",
                                    summary = "선호 동물 종류: 상관없음",
                                    value = """
                    {
                      "animalType": "ANY",
                      "size": "MEDIUM",
                      "gender": "ANY",
                      "age": "ADULT",
                      "personalities": "온순함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "size_small",
                                    summary = "선호 크기: 소형",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "SMALL",
                      "gender": "FEMALE",
                      "age": "ADULT",
                      "personalities": "사람 좋아함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "size_medium",
                                    summary = "선호 크기: 중형",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "MEDIUM",
                      "gender": "MALE",
                      "age": "ADULT",
                      "personalities": "활발함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "size_large",
                                    summary = "선호 크기: 대형",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "LARGE",
                      "gender": "MALE",
                      "age": "SENIOR",
                      "personalities": "온순함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "size_any",
                                    summary = "선호 크기: 상관없음",
                                    value = """
                    {
                      "animalType": "CAT",
                      "size": "ANY",
                      "gender": "FEMALE",
                      "age": "YOUNG",
                      "personalities": "호기심 많음"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "gender_male",
                                    summary = "선호 성별: 수컷",
                                    value = """
                    {
                      "animalType": "CAT",
                      "size": "SMALL",
                      "gender": "MALE",
                      "age": "ADULT",
                      "personalities": "사람 좋아함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "gender_female",
                                    summary = "선호 성별: 암컷",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "MEDIUM",
                      "gender": "FEMALE",
                      "age": "YOUNG",
                      "personalities": "활발함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "gender_any",
                                    summary = "선호 성별: 상관없음",
                                    value = """
                    {
                      "animalType": "ANY",
                      "size": "MEDIUM",
                      "gender": "ANY",
                      "age": "SENIOR",
                      "personalities": "온순함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "age_young",
                                    summary = "선호 연령대: 어린 개체",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "SMALL",
                      "gender": "FEMALE",
                      "age": "YOUNG",
                      "personalities": "호기심 많음"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "age_adult",
                                    summary = "선호 연령대: 성체",
                                    value = """
                    {
                      "animalType": "CAT",
                      "size": "MEDIUM",
                      "gender": "MALE",
                      "age": "ADULT",
                      "personalities": "사람 좋아함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "age_senior",
                                    summary = "선호 연령대: 노령 개체",
                                    value = """
                    {
                      "animalType": "DOG",
                      "size": "LARGE",
                      "gender": "ANY",
                      "age": "SENIOR",
                      "personalities": "온순함"
                    }
                    """
                            ),
                            @ExampleObject(
                                    name = "age_any",
                                    summary = "선호 연령대: 상관없음",
                                    value = """
                    {
                      "animalType": "CAT",
                      "size": "ANY",
                      "gender": "ANY",
                      "age": "ANY",
                      "personalities": "활발함"
                    }
                    """
                            )
                    }
            )
    )
    @PatchMapping("/preferences")
    public ResponseEntity<AnimalPreferenceResponse> patchUserPreferences(
            @RequestBody AnimalPreferenceRequest request
    ){
        Long userId = authService.getCurrentUserId();
        AnimalPreferenceResponse response = userPreferenceService.patchUserPreferences(userId, request);
        return ResponseEntity.ok(response);
    }

}
