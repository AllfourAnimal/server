package com.All4Animal.server.controller;


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

    @GetMapping("/recommendations")
    public ResponseEntity<?> getUserRecommendations(){
        return ResponseEntity.ok().build();
    }

}
