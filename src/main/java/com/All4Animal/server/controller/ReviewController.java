package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.response.ReviewDetailResponse;
import com.All4Animal.server.dto.response.ReviewListResponse;
import com.All4Animal.server.dto.response.ReviewResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@Tag(name = "Review", description = "리뷰 API")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    @GetMapping("/all")
    public ResponseEntity<?> getALlReviews(){
        ReviewListResponse response = reviewService.getAllReviews();

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId){
        ReviewDetailResponse response = reviewService.getReview(reviewId);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest request){
        Long userId = authService.getCurrentUserId();
        ReviewResponse response = reviewService.postReview(userId, request);

        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId){
        ReviewDetailResponse response = reviewService.DeleteReview(reviewId);
        return ResponseEntity.ok().body(response);
    }
}
