package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.AnimalSearchRequest;
import com.All4Animal.server.dto.response.AnimalFilterResponse;
import com.All4Animal.server.dto.response.AnimalResponse;
import com.All4Animal.server.dto.response.AnimalSearchResponse;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.AnimalImage;
import com.All4Animal.server.service.AnimalService;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@Tag(name = "Animal", description = "유기동물 관련 API")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @Operation(summary = "공공데이터 동기화", description = "동물 API를 DB에 저장")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "동기화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                  {
                                    "message": "동기화 및 DB 저장"
                                  }
                                  """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "server_error",
                                    value = """
                                  {
                                    "code": "INTERNAL_SERVER_ERROR",
                                    "message": "서버 내부 오류가 발생했습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    @GetMapping("/sync")
    public ResponseEntity<?> syncAnimals() {
        try {
            animalService.syncAnimalsWithApi();
            return ResponseEntity.ok(Map.of("message", "동기화 및 DB 저장"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "동물 목록 전체 조회", description = "DB에 저장된 동물 데이터 가져옴.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "동물 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                  [
                                    {
                                      "animalId": 1,
                                      "desertionNo": "123456789",
                                      "animalType": "DOG",
                                      "species": "푸들",
                                      "weight": 4.5,
                                      "animal_age": 2021,
                                      "persona": null,
                                      "animal_sex": "MALE",
                                      "description": "온순함",
                                      "isAdopted": false,
                                      "happenPlace": "서울특별시 강남구",
                                      "careNm": "강남동물보호센터",
                                      "careTel": "02-1234-5678",
                                      "careAddr": "서울특별시 강남구"
                                    }
                                  ]
                                  """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "server_error",
                                    value = """
                                  {
                                    "code": "INTERNAL_SERVER_ERROR",
                                    "message": "서버 내부 오류가 발생했습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    public ResponseEntity<List<AnimalResponse>> getAllAnimals() {
        List<AnimalResponse> animals = animalService.getAllAnimals();
        return ResponseEntity.ok(animals);
    }

    @GetMapping("/{animalId}/images")
    @Operation(summary = "특정 동물의 이미지 조회", description = "동물 ID를 이용해 해당 동물의 모든 이미지 URL을 가져옵니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 URL 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                  [
                                    "https://example.com/animal1.jpg",
                                    "https://example.com/animal2.jpg"
                                  ]
                                  """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 동물을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "not_found",
                                    value = """
                                  {
                                    "code": "NOT_FOUND",
                                    "message": "해당 동물을 찾을 수 없습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    public ResponseEntity<List<String>> getAnimalImages(@PathVariable Long animalId) {
        List<AnimalImage> images = animalService.getImageByAnimalId(animalId);

        List<String> imageUrls = images.stream()
                .map(AnimalImage::getImageUrl)
                .toList();

        return ResponseEntity.ok(imageUrls);
    }

    @GetMapping("/search")
    @Operation(summary = "동물 검색 필터링", description = "사용자가 입력한 키워드, 동물의 종, 보호 지역을 기준으로 동물을 필터링합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "동물 검색 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AnimalFilterResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                                  [
                                    {
                                      "animalId": 1,
                                      "animalType": "DOG",
                                      "species": "푸들",
                                      "description": "온순함",
                                      "happenPlace": "서울특별시 강남구",
                                      "careNm": "강남동물보호센터",
                                      "careAddr": "서울특별시 강남구"
                                    }
                                  ]
                                  """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 검색 조건",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "bad_request",
                                    value = """
                                  {
                                    "code": "BAD_REQUEST",
                                    "message": "검색 조건이 올바르지 않습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    public ResponseEntity<List<AnimalSearchResponse>> searchAniamls(@ModelAttribute AnimalSearchRequest request) {
        List<AnimalSearchResponse> animals = animalService.searchAnimals(request);

        return ResponseEntity.ok(animals);
    }
}