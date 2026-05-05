package com.All4Animal.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ReviewRequest {

    @Schema(description = "리뷰 제목", example = "입양 후기")
    private String title;

    @Schema(description = "반려동물 이름", example = "초코")
    private String petName;

    @JsonProperty("desertion_no")
    @Schema(description = "공고 번호. 선택 입력값입니다.", example = "441111202600123")
    private String desertionNo;

    @Schema(description = "리뷰 내용", example = "적응도 빠르고 사람을 잘 따라요.")
    private String content;

    @Schema(description = "리뷰 대상 동물 ID", example = "12")
    private Long animalId;

    @Schema(description = "동물 이미지 URL", example = "https://example.com/animals/12.jpg")
    private String animalUrl;
}
