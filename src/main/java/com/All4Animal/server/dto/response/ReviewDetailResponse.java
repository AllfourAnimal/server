package com.All4Animal.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReviewDetailResponse {
    @Schema(description = "리뷰 ID", example = "1")
    private Long reviewId;

    @Schema(description = "리뷰 제목", example = "입양 후기")
    private String title;

    @Schema(description = "반려동물 이름", example = "초코")
    private String petName;

    @Schema(description = "리뷰 내용", example = "산책을 정말 좋아해요.")
    private String content;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String username;

    @Schema(description = "공고 번호", example = "4411112")
    private String desertion_no;

    @Schema(description = "구조 위치", example = "경기도 하남시 하남시청역 근방")
    private String happenPlace;

    @Schema(description = "종에 대한 정보", example = "푸들")
    private String species;

    @JsonProperty("is_adopted")
    @Schema(description = "입양 완료 여부", example = "true")
    private boolean adopted;

    @Schema(description = "입양 완료 날짜. 입양 완료 내역이 없으면 null", example = "2026-05-10T12:30:00")
    private LocalDateTime adoptedAt;

    @Schema(description = "작성 시각", example = "2026-04-10")
    private LocalDateTime createdAt;

    @Schema(description = "S3 이미지 key", example = "review/1/550e8400-e29b-41d4-a716-446655440000/cat.png")
    private String imageKey;

    @Schema(description = "이미지 조회용 Presigned URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/...")
    private String imageUrl;
}
