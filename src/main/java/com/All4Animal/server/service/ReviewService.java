package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import com.All4Animal.server.dto.response.*;
import com.All4Animal.server.entity.Adoption;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Review;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AdoptionRepository;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.ReviewRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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
    private final AdoptionRepository adoptationRepository;
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
                        Adoption.AdoptionStatus.COMPLETED
                ).stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));
        return new ReviewDetailResponse(
                review.getReviewId(),
                review.getTitle(),
                review.getPetName(),
                review.getContent(),
                review.getUserId(),
                review.getUsername(),
                review.getDesertionNo(),
                review.getHappenPlace(),
                review.getSpecies(),
                review.isAdopted(),
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

        Animal animal = findReviewAnimal(request);

        boolean hasAdoptionHistory = adoptationRepository.existsByUserAndAnimalAndStatusIn(
                user,
                animal,
                List.of(
                        Adoption.AdoptionStatus.INQUIRY,
                        Adoption.AdoptionStatus.APPLIED,
                        Adoption.AdoptionStatus.COMPLETED
                )
        );
        if (!hasAdoptionHistory) {
            throw new IllegalArgumentException("입양 문의 또는 입양 신청 이력이 있는 동물만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByUserAndAnimal(user, animal)) {
            throw new IllegalArgumentException("이미 해당 동물에 대한 리뷰를 작성했습니다.");
        }

        S3PresignedUrlResponse imageUploadResponse = null;
        if (image != null && !image.isEmpty()) {
            imageUploadResponse = s3Service.uploadReviewImage(userId, image);
        }

        Review review = Review.builder()
                .title(request.getTitle())
                .petName(request.getPetName())
                .desertionNo(animal.getDesertionNo())
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

    public DeleteReviewResponse DeleteReview(Long userId, Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }

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

    private Animal findReviewAnimal(ReviewRequest request) {
        if (StringUtils.hasText(request.getDesertionNo())) {
            return animalRepository.findByDesertionNo(request.getDesertionNo())
                    .orElseThrow(() -> new IllegalArgumentException("해당 공고 번호의 동물이 존재하지 않습니다."));
        }

        throw new IllegalArgumentException("리뷰를 작성하려면 공고 번호가 필요합니다.");
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
