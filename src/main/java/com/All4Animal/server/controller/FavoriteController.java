package com.All4Animal.server.controller;

import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.FavoriteResponse;
import com.All4Animal.server.service.FavoriteService;
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

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜하기 API")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "찜하기 토글", description = "로그인한 사용자가 특정 동물을 찜 목록에 추가하거나 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "토글 작업 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = {
                                    @ExampleObject(name = "찜 등록", value = "찜 목록에 추가되었습니다."),
                                    @ExampleObject(name = "찜 취소", value = "찜 목록에서 삭제되었습니다.")
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패 (로그인 필요)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                  {
                                    "code": "UNAUTHORIZED",
                                    "message": "로그인이 필요한 기능입니다."
                                  }
                                  """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "동물 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                  {
                                    "code": "ANIMAL_NOT_FOUND",
                                    "message": "존재하지 않는 동물 ID입니다."
                                  }
                                  """
                            )
                    )
            )
    })
    @PostMapping("/{animalId}")
    public ResponseEntity<String> toggleFavorite(@PathVariable Long animalId) {
        String result = favoriteService.toggleFavorite(animalId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "내 찜 목록 조회", description = "현재 로그인된 사용자가 찜한 모든 유기동물 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "speices": "강아지",
                                                "thumbnailImageUrl": "http://example.image.url/test1.jpg",
                                                "animalAge": 2023,
                                                "animlSex": "FEMALE",
                                                "animalStory": "동물 세부사항 예시"
                                              },
                                              {
                                                "speices": "고양이",
                                                "thumbnailImageUrl": "http://example.image.url/test2.jpg",
                                                "animalAge": 2026,
                                                "animlSex": "MALE",
                                                "animalStory": "동물 세부사항 예시"
                                              }
                                            ]
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
    @GetMapping("/my_favorite")
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites() {
        List<FavoriteResponse> responses = favoriteService.getMyFavoriteAnimals();
        return ResponseEntity.ok(responses);
    }
}