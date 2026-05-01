package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Animal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendedAnimalResponse {
    @Schema(description = "동물 ID", example = "12")
    private Long animalId;

    @Schema(description = "유기번호", example = "448500202600123")
    private String desertionNo;

    @Schema(description = "동물 종류", example = "DOG")
    private Animal.AnimalType animalType;

    @Schema(description = "품종", example = "믹스견")
    private String species;

    @Schema(description = "몸무게", example = "8.4")
    private double weight;

    @Schema(description = "출생년도", example = "2024")
    private Integer animalAge;

    @Schema(description = "성격 요약", example = "사람을 좋아하고 활발함")
    private String persona;

    @Schema(description = "성별", example = "FEMALE")
    private Animal.Gender animalSex;

    @Schema(description = "예방접종 여부", example = "false")
    private boolean isVaccinated;

    @Schema(description = "상세 설명", example = "온순하고 사람을 잘 따르며 산책을 좋아함")
    private String description;

    @Schema(description = "입양 여부", example = "false")
    private boolean isAdopted;

    @Schema(description = "등록 시각", example = "2026-05-01T09:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "발견 장소", example = "서울특별시 마포구")
    private String happenPlace;

    @Schema(description = "보호소 이름", example = "마포 보호소")
    private String careNm;

    @Schema(description = "보호소 연락처", example = "02-123-4567")
    private String careTel;

    @Schema(description = "보호소 주소", example = "서울특별시 마포구 상암동")
    private String careAddr;

    @Schema(description = "동물 이미지 URL 목록")
    private List<String> imageUrls;
}
