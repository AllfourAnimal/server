package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.ReviewRequest;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.dto.response.ReviewDetailResponse;
import com.All4Animal.server.dto.response.ReviewListResponse;
import com.All4Animal.server.dto.response.ReviewResponse;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
                          "username": "홍길동"
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
                      "createdAt": "2026-04-10T10:30:00"
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
                      "createdAt": "2026-04-10T10:30:00"
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
    @PostMapping("")
    public ResponseEntity<?> postReview(@RequestBody ReviewRequest request){
        Long userId = authService.getCurrentUserId();
        ReviewResponse response = reviewService.postReview(userId, request);

        return ResponseEntity.ok().body(response);
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
                      "createdAt": "2026-04-10T10:30:00"
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
        ReviewDetailResponse response = reviewService.DeleteReview(reviewId);
        return ResponseEntity.ok().body(response);
    }
}
