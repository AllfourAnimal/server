package com.All4Animal.server.controller;

import com.All4Animal.server.dto.response.FavoriteResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜하기 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{animalId}")
    @Operation(summary = "찜하기 토글", description = "이미 찜했으면 취소, 안 했으면 등록합니다.")
    public ResponseEntity<String> toggleFavorite(
            @PathVariable Long animalId) {
        String result = favoriteService.toggleFavorite(animalId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my_favorite")
    @Operation(summary = "내 찜 목록 조회", description = "로그인 된 사용자가 찜한 모든 동물 반환")
    public ResponseEntity<List<FavoriteResponse>> getMyFavorites() {
        List<FavoriteResponse> responses = favoriteService.getMyFavoriteAnimals();
        return ResponseEntity.ok(responses);
    }


//    public ResponseEntity<List<Animal>> getMyFavorites() {
//        return ResponseEntity.ok(favoriteService.getMyFavoriteAnimals());
//    }
}
