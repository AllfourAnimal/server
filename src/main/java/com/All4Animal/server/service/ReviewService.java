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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
//                createImageUrl(review.getImageKey()),
                createImageUrls(review.getImageKey())
        );
    }

    public ReviewResponse postReview(Long userId, ReviewRequest request){
        return postReview(userId, request, null);
    }

    public ReviewResponse postReview(Long userId, ReviewRequest request, List<MultipartFile> images){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 존재하지 않습니다."));

        Animal animal = findReviewAnimal(request);

        boolean hasAdoptionHistory = adoptationRepository.existsByUserAndAnimalAndStatusIn(
                user,
                animal,
                List.of(
                        Adoption.AdoptionStatus.INQUIRY,
                        Adoption.AdoptionStatus.COMPLETED
                )
        );
        if (!hasAdoptionHistory) {
            throw new IllegalArgumentException("입양 문의 이력이 있는 동물만 리뷰를 작성할 수 있습니다.");
        }

        if (reviewRepository.existsByUserAndAnimal(user, animal)) {
            throw new IllegalArgumentException("이미 해당 동물에 대한 리뷰를 작성했습니다.");
        }

        List<S3PresignedUrlResponse> imageUploadResponses = uploadReviewImages(userId, images);
        String imageKeys = joinImageKeys(imageUploadResponses);

        Review review = Review.builder()
                .title(request.getTitle())
                .petName(request.getPetName())
                .desertionNo(animal.getDesertionNo())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .imageKey(imageKeys)
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
                firstImageUrl(imageUploadResponses),
                imageUploadResponses.stream()
                        .map(S3PresignedUrlResponse::getPreSignedUrl)
                        .toList()
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
                createImageUrl(review.getImageKey()),
                createImageUrls(review.getImageKey())
        );

        reviewRepository.delete(review);
        deleteReviewImage(review.getImageKey());

        return response;

    }

    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request) {
        return updateReview(userId, reviewId, request, null, null);
    }

    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewRequest request, List<MultipartFile> images) {
        return updateReview(userId, reviewId, request, null, images);
    }

    public ReviewResponse updateReview(
            Long userId,
            Long reviewId,
            ReviewRequest request,
            List<String> imageUrls,
            List<MultipartFile> images
    ) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰가 존재하지 않습니다."));

        if (!review.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getPetName() != null) {
            review.setPetName(request.getPetName());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }

        List<String> currentImageKeys = splitImageKeys(review.getImageKey());
        List<String> retainedImageKeys = resolveRetainedImageKeys(imageUrls, currentImageKeys);
        List<MultipartFile> uploadImages = normalizeImages(images);

        if (retainedImageKeys.size() + uploadImages.size() > 3) {
            throw new IllegalArgumentException("리뷰 이미지는 최대 3장까지 유지할 수 있습니다.");
        }

        boolean imageListRequested = imageUrls != null || !uploadImages.isEmpty();
        List<S3PresignedUrlResponse> imageUploadResponses = List.of();
        if (imageListRequested) {
            deleteRemovedImages(currentImageKeys, retainedImageKeys);
            imageUploadResponses = uploadReviewImages(userId, uploadImages);

            List<String> finalImageKeys = new ArrayList<>(retainedImageKeys);
            finalImageKeys.addAll(imageUploadResponses.stream()
                    .map(S3PresignedUrlResponse::getKey)
                    .toList());
            review.setImageKey(joinImageKeysFromKeys(finalImageKeys));
        }

        reviewRepository.save(review);

        return new ReviewResponse(
                review.getReviewId(),
                review.getTitle(),
                review.getPetName(),
                review.getDesertionNo(),
                review.getContent(),
                review.getCreatedAt(),
                review.getImageKey(),
                createImageUrl(review.getImageKey()),
                createImageUrls(review.getImageKey())
        );
    }

    private void deleteReviewImage(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return;
        }

        for (String key : splitImageKeys(imageKey)) {
            try {
                s3Service.deleteFile(key);
            } catch (Exception exception) {
                log.warn("S3 리뷰 이미지 삭제 실패. key={}, reason={}", key, exception.getMessage(), exception);
            }
        }
    }

    private String createImageUrl(String imageKey) {
        return createImageUrls(imageKey).stream()
                .findFirst()
                .orElse(null);
    }

    private List<String> createImageUrls(String imageKey) {
        return splitImageKeys(imageKey).stream()
                .map(key -> s3Service.getGetS3Url(0L, key).getPreSignedUrl())
                .toList();
    }

    private List<String> splitImageKeys(String imageKey) {
        if (imageKey == null || imageKey.isBlank()) {
            return List.of();
        }

        List<String> keys = new ArrayList<>();
        for (String key : imageKey.split(",")) {
            if (StringUtils.hasText(key)) {
                keys.add(key.trim());
            }
        }
        return keys;
    }

    private List<S3PresignedUrlResponse> uploadReviewImages(Long userId, List<MultipartFile> images) {
        List<MultipartFile> uploadImages = normalizeImages(images);
        if (uploadImages.size() > 3) {
            throw new IllegalArgumentException("리뷰 이미지는 최대 3장까지 업로드할 수 있습니다.");
        }

        List<S3PresignedUrlResponse> responses = new ArrayList<>();
        for (MultipartFile image : uploadImages) {
            responses.add(s3Service.uploadReviewImage(userId, image));
        }
        return responses;
    }

    private List<MultipartFile> normalizeImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        List<MultipartFile> uploadImages = new ArrayList<>();
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                uploadImages.add(image);
            }
        }
        return uploadImages;
    }

    private String joinImageKeys(List<S3PresignedUrlResponse> imageUploadResponses) {
        if (imageUploadResponses.isEmpty()) {
            return null;
        }

        return String.join(
                ",",
                imageUploadResponses.stream()
                        .map(S3PresignedUrlResponse::getKey)
                        .toList()
        );
    }

    private String joinImageKeysFromKeys(List<String> imageKeys) {
        if (imageKeys == null || imageKeys.isEmpty()) {
            return null;
        }
        return String.join(",", imageKeys);
    }

    private List<String> resolveRetainedImageKeys(List<String> imageUrls, List<String> currentImageKeys) {
        if (imageUrls == null) {
            return currentImageKeys;
        }

        Set<String> retainedKeys = new LinkedHashSet<>();
        for (String imageUrl : imageUrls) {
            if (!StringUtils.hasText(imageUrl)) {
                continue;
            }

            String normalizedImageUrl = normalizeImageReference(imageUrl);
            for (String currentImageKey : currentImageKeys) {
                if (normalizedImageUrl.equals(currentImageKey) || normalizedImageUrl.contains(currentImageKey)) {
                    retainedKeys.add(currentImageKey);
                    break;
                }
            }
        }

        return new ArrayList<>(retainedKeys);
    }

    private String normalizeImageReference(String imageUrl) {
        String value = imageUrl.trim();
        int queryStartIndex = value.indexOf('?');
        if (queryStartIndex >= 0) {
            value = value.substring(0, queryStartIndex);
        }

        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            return value;
        }
    }

    private void deleteRemovedImages(List<String> currentImageKeys, List<String> retainedImageKeys) {
        for (String currentImageKey : currentImageKeys) {
            if (!retainedImageKeys.contains(currentImageKey)) {
                deleteReviewImage(currentImageKey);
            }
        }
    }

    private String firstImageUrl(List<S3PresignedUrlResponse> imageUploadResponses) {
        return imageUploadResponses.stream()
                .findFirst()
                .map(S3PresignedUrlResponse::getPreSignedUrl)
                .orElse(null);
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
                            createImageUrl(review.getImageKey()),
                            createImageUrls(review.getImageKey())
                    );
            responses.add(response);
        }

        return new ReviewListResponse(reviews.size(), responses);
    }

}
