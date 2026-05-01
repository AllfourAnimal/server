package com.All4Animal.server.controller;


import com.All4Animal.server.dto.response.AnimalFilterResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.RecommendationService;
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

    @GetMapping("/recommendations")
    public ResponseEntity<?> getUserRecommendations(){
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/filter")
//    public ResponseEntity<?> getAnimalFilter(){
//        Long userId = authService.getCurrentUserId();
//        AnimalFilterResponse response =  recommendationService.getAnimalFilter(userId);
//        return ResponseEntity.ok(response);
//    }

}
