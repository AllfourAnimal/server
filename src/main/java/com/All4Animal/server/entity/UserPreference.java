package com.All4Animal.server.entity;

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

    public enum PreferredAnimalType {
        DOG,        // 강아지
        CAT,        // 고양이
        ANY         // 상관없음
    }

    public enum PreferredSize {
        SMALL,      // 소형
        MEDIUM,     // 중형
        LARGE,      // 대형
        ANY         // 상관없음
    }

    public enum PreferredGender {
        MALE,       // 남
        FEMALE,     // 여
        ANY         // 상관없음
    }

    public enum PreferredNeuteredStatus {
        YES,        // 중성화 됨
        NO,         // 중성화 안 됨
        ANY         // 상관없음
    }

    public enum PreferredAgeGroup {
        YOUNG,      // 어린이
        ADULT,      // 보통
        SENIOR,     // 노인
        ANY         // 상관없음
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    private PreferredAnimalType preferredAnimalType;

    @Enumerated(EnumType.STRING)
    private PreferredSize preferredSize;

    @Enumerated(EnumType.STRING)
    private PreferredGender preferredGender;

    @Enumerated(EnumType.STRING)
    private PreferredNeuteredStatus preferredNeuteredStatus;

    @Enumerated(EnumType.STRING)
    private PreferredAgeGroup preferredAgeGroup;

    private String preferredPersonality;

}
