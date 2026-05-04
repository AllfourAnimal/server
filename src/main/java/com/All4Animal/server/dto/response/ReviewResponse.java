package com.All4Animal.server.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "리뷰 제목", example = "입양 후기")
    private String title;

    @Schema(description = "리뷰 내용", example = "적응도 빠르고 애교가 많아요.")
    private String content;

    @Schema(description = "작성 시각", example = "2026-05-04T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "S3 이미지 key", example = "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png")
    private String imageKey;

    @Schema(description = "이미지 조회용 Presigned URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/...")
    private String imageUrl;
}
