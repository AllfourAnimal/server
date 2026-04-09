package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.response.*;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Review;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.ReviewRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;


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

        return new ReviewDetailResponse(
                review.getReviewId(), review.getTitle(), review.getContent(), review.getCreatedAt());
    }

    public ReviewResponse postReview(Long userId, ReviewRequest request){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 동물이 존재하지 않습니다."));
        Review review = Review.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .animal(animal).build();

        reviewRepository.save(review);

        return new ReviewResponse(
                review.getReviewId(), review.getTitle(), review.getContent(), review.getCreatedAt());
    }

    public ReviewDetailResponse DeleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        ReviewDetailResponse response = new ReviewDetailResponse(
                review.getReviewId(), review.getTitle(), review.getContent(), review.getCreatedAt());

        reviewRepository.delete(review);

        return response;

    }

}
