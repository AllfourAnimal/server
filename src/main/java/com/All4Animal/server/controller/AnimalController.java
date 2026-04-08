package com.All4Animal.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/animals")
@Tag(name = "Animal", description = "유기동물 관련 API")
@RequiredArgsConstructor
public class AnimalController {

    @Operation(summary = "공공데이터 동기화", description = "동물 API를 DB에 저장")
    @GetMapping("/sync")
    public ResponseEntity<?> syncAnimals() {
        try {
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }
}
