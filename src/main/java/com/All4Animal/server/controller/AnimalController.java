package com.All4Animal.server.controller;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.service.AnimalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/animals")
@Tag(name = "Animal", description = "유기동물 관련 API")
@RequiredArgsConstructor
public class AnimalController {

    private final AnimalService animalService;

    @Operation(summary = "공공데이터 동기화", description = "동물 API를 DB에 저장")
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
    public ResponseEntity<List<Animal>> getAllAnimals() {
        List<Animal> animals = animalService.getAllAnimals();
        return ResponseEntity.ok(animals);
    }
}