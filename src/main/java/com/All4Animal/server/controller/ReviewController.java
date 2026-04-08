package com.All4Animal.server.controller;

import com.All4Animal.server.dto.response.ReviewDetailResponse;
import com.All4Animal.server.dto.response.ReviewListResponse;
import com.All4Animal.server.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

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
}
