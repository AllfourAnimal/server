package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import com.All4Animal.server.dto.response.*;
import com.All4Animal.server.entity.Adoptation;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Review;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AdoptationRepository;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.ReviewRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final AnimalRepository animalRepository;
    private final AdoptationRepository adoptationRepository;
    private final S3Service s3Service;


    public ReviewListResponse getAllReviews(){
        return toReviewListResponse(reviewRepository.findAllWithUser());
    }

    public ReviewListResponse getReviewsByAnimalType(Animal.AnimalType animalType) {
        return toReviewListResponse(reviewRepository.findAllByAnimalTypeWithUserAndAnimal(animalType));
    }

    public ReviewListResponse getAdoptedAnimalReviews() {
        return toReviewListResponse(reviewRepository.findAllByAdoptedAnimalWithUserAndAnimal());
    }

    public ReviewDetailResponse getReview(Long reviewId){
        ReviewDetailDto review = reviewRepository.findReviewDetailDtoById(
                        reviewId,
                        Adoptation.AdoptionStatus.COMPLETED
                ).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        return new ReviewDetailResponse(
                review.getReviewId(),
                review.getTitle(),
                review.getContent(),
                review.getDesertionNo(),
                review.getHappenPlace(),
                review.getSpecies(),
                review.getAdoptedAt(),
                review.getCreatedAt(),
                review.getImageKey(),
                createImageUrl(review.getImageKey())
        );
    }

    public ReviewResponse postReview(Long userId, ReviewRequest request){
        return postReview(userId, request, null);
    }

    public ReviewResponse postReview(Long userId, ReviewRequest request, MultipartFile image){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Animal animal = animalRepository.findById(request.getAnimalId())
                .orElseThrow(() -> new IllegalArgumentException("해당 동물이 존재하지 않습니다."));

        S3PresignedUrlResponse imageUploadResponse = null;
        if (image != null && !image.isEmpty()) {
            imageUploadResponse = s3Service.uploadReviewImage(userId, image);
        }

        Review review = Review.builder()
                .title(request.getTitle())
                .petName(request.getPetName())
                .desertionNo(request.getDesertionNo())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .imageKey(imageUploadResponse != null ? imageUploadResponse.getKey() : null)
                .user(user)
                .animal(animal).build();

        reviewRepository.save(review);

        return new ReviewResponse(
                review.getReviewId(),
                review.getTitle(),
                review.getPetName(),
                review.getDesertionNo(),
                review.getContent(),
                review.getCreatedAt(),
                review.getImageKey(),
                imageUploadResponse != null ? imageUploadResponse.getPreSignedUrl() : null
        );
    }

    public DeleteReviewResponse DeleteReview(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        DeleteReviewResponse response = new DeleteReviewResponse(
                review.getReviewId(),
                review.getTitle(),
                review.getContent(),
                review.getCreatedAt(),
                review.getImageKey(),
                createImageUrl(review.getImageKey())
        );

        reviewRepository.delete(review);
        deleteReviewImage(review.getImageKey());

        return response;

    }

    private void deleteReviewImage(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return;
        }

        try {
            s3Service.deleteFile(imageKey);
        } catch (Exception exception) {
            log.warn("S3 리뷰 이미지 삭제 실패. key={}, reason={}", imageKey, exception.getMessage(), exception);
        }
    }

    private String createImageUrl(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return s3Service.getGetS3Url(0L, imageKey).getPreSignedUrl();
    }

    private ReviewListResponse toReviewListResponse(List<Review> reviews) {
        List<ReviewItemResponse> responses = new ArrayList<>();

        for (Review review : reviews) {
            Users user = review.getUser();
            Animal animal = review.getAnimal();
            ReviewItemResponse response =
                    new ReviewItemResponse(
                            review.getReviewId(),
                            review.getTitle(),
                            review.getContent(),
                            review.getCreatedAt(),
                            user.getUserId(),
                            user.getUsername(),
                            animal.getAnimalId(),
                            animal.getAnimalType(),
                            animal.isAdopted(),
                            review.getImageKey(),
                            createImageUrl(review.getImageKey())
                    );
            responses.add(response);
        }

        return new ReviewListResponse(reviews.size(), responses);
    }

}
