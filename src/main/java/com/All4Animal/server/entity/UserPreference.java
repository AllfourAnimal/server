package com.All4Animal.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "UserPreference")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserPreference {

    @Schema(description = "선호 동물 종류")
    public enum PreferredAnimalType {
        @Schema(description = "강아지")
        DOG,        // 강아지
        @Schema(description = "고양이")
        CAT,        // 고양이
        @Schema(description = "상관없음")
        ANY         // 상관없음
    }

    @Schema(description = "선호 동물 크기")
    public enum PreferredSize {
        @Schema(description = "소형")
        SMALL,      // 소형
        @Schema(description = "중형")
        MEDIUM,     // 중형
        @Schema(description = "대형")
        LARGE,      // 대형
        @Schema(description = "상관없음")
        ANY         // 상관없음
    }

    @Schema(description = "선호 동물 성별")
    public enum PreferredGender {
        @Schema(description = "수컷")
        MALE,       // 남
        @Schema(description = "암컷")
        FEMALE,     // 여
        @Schema(description = "상관없음")
        ANY         // 상관없음
    }

    @Schema(description = "선호 동물 연령대")
    public enum PreferredAgeGroup {
        @Schema(description = "어린 개체")
        YOUNG,      // 어린이
        @Schema(description = "성체")
        ADULT,      // 보통
        @Schema(description = "노령 개체")
        SENIOR,     // 노인
        @Schema(description = "상관없음")
        ANY         // 상관없음
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Schema(description = "선호 동물 종류")
    private PreferredAnimalType preferredAnimalType;

    @Enumerated(EnumType.STRING)
    @Schema(description = "선호 동물 크기")
    private PreferredSize preferredSize;

    @Enumerated(EnumType.STRING)
    @Schema(description = "선호 동물 성별")
    private PreferredGender preferredGender;

    @Enumerated(EnumType.STRING)
    @Schema(description = "선호 동물 연령대")
    private PreferredAgeGroup preferredAgeGroup;

    @Schema(description = "선호 성격 키워드")
    private String preferredPersonality;


}
