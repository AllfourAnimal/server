package com.All4Animal.server.controller;


import com.All4Animal.server.dto.response.AnimalFilterResponse;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Recommendation", description = "추천 API")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final AuthService authService;

    @Operation(summary = "맞춤 동물 추천", description = "사용자의 선호 정보와 생활 패턴을 기반으로 상위 3마리 동물을 추천합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "추천 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalFilterResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "count": 3,
                      "animals": [
                        {
                          "animalId": 12,
                          "desertionNo": "448500202600123",
                          "animalType": "DOG",
                          "species": "믹스견",
                          "weight": 8.4,
                          "animal_age": 2024,
                          "persona": "사람을 좋아하고 활발함",
                          "animal_sex": "FEMALE",
                          "isVaccinated": false,
                          "description": "온순하고 사람을 잘 따르며 산책을 좋아함",
                          "isAdopted": false,
                          "createdAt": "2026-05-01T09:30:00",
                          "happenPlace": "서울특별시 마포구",
                          "careNm": "마포 보호소",
                          "careTel": "02-123-4567",
                          "careAddr": "서울특별시 마포구 상암동",
                          "images": []
                        },
                        {
                          "animalId": 28,
                          "desertionNo": "448500202600456",
                          "animalType": "DOG",
                          "species": "푸들",
                          "weight": 6.2,
                          "animal_age": 2023,
                          "persona": "애교 많고 호기심이 많음",
                          "animal_sex": "MALE",
                          "isVaccinated": true,
                          "description": "사람을 좋아하고 적응이 빠름",
                          "isAdopted": false,
                          "createdAt": "2026-05-01T08:10:00",
                          "happenPlace": "서울특별시 은평구",
                          "careNm": "은평 보호소",
                          "careTel": "02-555-1234",
                          "careAddr": "서울특별시 은평구 녹번동",
                          "images": []
                        },
                        {
                          "animalId": 35,
                          "desertionNo": "448500202600789",
                          "animalType": "DOG",
                          "species": "비숑",
                          "weight": 7.8,
                          "animal_age": 2022,
                          "persona": "활발하고 사람을 좋아함",
                          "animal_sex": "FEMALE",
                          "isVaccinated": true,
                          "description": "얌전한 편이지만 친화력이 좋음",
                          "isAdopted": false,
                          "createdAt": "2026-04-30T18:20:00",
                          "happenPlace": "경기도 고양시",
                          "careNm": "고양 보호소",
                          "careTel": "031-123-4567",
                          "careAddr": "경기도 고양시 일산동구",
                          "images": []
                        }
                      ]
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "추천 조건 부족",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "preference_not_found",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "선호 데이터가 없습니다."
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
                                    name = "unauthorized",
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
    @GetMapping("/recommendations")
    public ResponseEntity<AnimalFilterResponse> getUserRecommendations(){
        Long userId = authService.getCurrentUserId();
        AnimalFilterResponse response = recommendationService.recommendTop3AnimalsByPreference(userId);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/filter")
//    public ResponseEntity<?> getAnimalFilter(){
//        Long userId = authService.getCurrentUserId();
//        AnimalFilterResponse response =  recommendationService.getAnimalFilter(userId);
//        return ResponseEntity.ok(response);
//    }

}
