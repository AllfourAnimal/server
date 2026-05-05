package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.response.*;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/review")
@Tag(name = "Review", description = "리뷰 API")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final AuthService authService;

    @Operation(summary = "리뷰 전체 조회", description = "등록된 리뷰 목록을 전체 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewListResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "count": 1,
                      "reviews": [
                        {
                          "reviewId": 1,
                          "title": "입양 후기",
                          "content": "활발하고 사람을 잘 따라요.",
                          "createdAt": "2026-04-10T10:30:00",
                          "userId": 1,
                          "username": "홍길동",
                          "imageKey": "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png",
                          "imageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/..."
                        }
                      ]
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/all")
    public ResponseEntity<?> getALlReviews(){
        ReviewListResponse response = reviewService.getAllReviews();

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 ID로 리뷰 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 상세 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "reviewId": 1,
                      "title": "입양 후기",
                      "content": "산책을 정말 좋아해요.",
                      "createdAt": "2026-04-10T10:30:00",
                      "imageKey": "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png",
                      "imageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/..."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 리뷰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "review_not_found",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해당 리뷰가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<?> getReview(@PathVariable Long reviewId){
        ReviewDetailResponse response = reviewService.getReview(reviewId);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "리뷰 작성", description = "로그인한 사용자가 리뷰를 작성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "reviewId": 1,
                      "title": "입양 후기",
                      "content": "적응도 빠르고 애교가 많아요.",
                      "createdAt": "2026-04-10T10:30:00",
                      "imageKey": null,
                      "imageUrl": null
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "user_not_found",
                                            value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해당 유저가 존재하지 않습니다."
                    }
                    """
                                    ),
                                    @ExampleObject(
                                            name = "animal_not_found",
                                            value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해당 동물이 존재하지 않습니다."
                    }
                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "unauthorized",
                                    value = """
                    {
                      "code": "UNAUTHORIZED",
                      "message": "인증 정보가 유효하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest request){
        Long userId = authService.getCurrentUserId();
        ReviewResponse response = reviewService.postReview(userId, request);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "리뷰 작성 - 사진 포함", description = "multipart/form-data로 리뷰 정보와 사진을 함께 받아 사진은 S3에 저장하고 리뷰에는 S3 key를 저장합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "사진 포함 리뷰 작성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "reviewId": 1,
                      "title": "입양 후기",
                      "content": "적응도 빠르고 애교가 많아요.",
                      "createdAt": "2026-05-04T10:30:00",
                      "imageKey": "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png",
                      "imageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/..."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "animal_not_found",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해당 동물이 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> postReviewWithImage(
            @Parameter(description = "리뷰 제목", example = "입양 후기")
            @RequestParam String title,
            @Parameter(description = "리뷰 내용", example = "적응도 빠르고 애교가 많아요.")
            @RequestParam String content,
            @Parameter(description = "리뷰 대상 동물 ID", example = "12")
            @RequestParam Long animalId,
            @Parameter(description = "리뷰 사진 파일")
            @RequestPart(value = "image", required = false) MultipartFile image
    ){
        Long userId = authService.getCurrentUserId();

        ReviewRequest request = new ReviewRequest();
        request.setTitle(title);
        request.setContent(content);
        request.setAnimalId(animalId);

        ReviewResponse response = reviewService.postReview(userId, request, image);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 ID로 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "리뷰 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReviewDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    value = """
                    {
                      "reviewId": 1,
                      "title": "입양 후기",
                      "content": "산책을 정말 좋아해요.",
                      "createdAt": "2026-04-10T10:30:00",
                      "imageKey": "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png",
                      "imageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/..."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "존재하지 않는 리뷰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "review_not_found",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해당 리뷰가 존재하지 않습니다."
                    }
                    """
                            )
                    )
            )
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId){
        DeleteReviewResponse response = reviewService.DeleteReview(reviewId);
        return ResponseEntity.ok().body(response);
    }
}
