package com.All4Animal.server.controller;

import com.All4Animal.server.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite", description = "찜하기 API")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{animalId}")
    @Operation(summary = "찜하기 토글", description = "이미 찜했으면 취소, 안 했으면 등록합니다.")
    public ResponseEntity<String> toggleFavorite(
            @RequestParam Long userId, // 테스트를 위해 파라미터로 받음
            @PathVariable Long animalId) {
        String result = favoriteService.toggleFavorite(userId, animalId);
        return ResponseEntity.ok(result);
    }
}
