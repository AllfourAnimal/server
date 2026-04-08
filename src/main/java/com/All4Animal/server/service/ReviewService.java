package com.All4Animal.server.service;

import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.ReviewDetailResponse;
import com.All4Animal.server.dto.response.ReviewItemResponse;
import com.All4Animal.server.dto.response.ReviewListResponse;
import com.All4Animal.server.entity.Review;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewListResponse getAllReviews(){
        List<ReviewItemResponse> responses = new ArrayList<>();
            List<Review> reviews = reviewRepository.findAllWithUser();
            for(Review review : reviews){
                Users user = review.getUser();
                ReviewItemResponse response =
                        new ReviewItemResponse(review.getReviewId(), review.getTitle(), review.getContent(), review.getCreatedAt(), user.getUserId(), user.getUsername());
                responses.add(response);
            }
            return new ReviewListResponse(reviews.size(), responses);
    }

    public ReviewDetailResponse getReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        ReviewDetailResponse response = new ReviewDetailResponse(
                review.getReviewId(), review.getTitle(), review.getContent(), review.getCreatedAt());

        return response;

    }
}
