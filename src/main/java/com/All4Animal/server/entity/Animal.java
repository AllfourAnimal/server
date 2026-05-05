package com.All4Animal.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity // Animal 클래스를 DB 테이블과 연결된 엔티티라고 선언
@Table(name = "Animal") // table 이름은 Animal로 설정
@NoArgsConstructor // 기본 생성자 자동 제작
@AllArgsConstructor // 전체 생성자 자동 제작
@Data // Getter, Setter, toString, equals과 같은 기본 메서드 자동 제작
@Builder // 빌더 패던 적용
public class Animal {
    public enum Gender{
        MALE,
        FEMALE,
        NEUTERED
    }

    public enum AnimalType {
        DOG,
        CAT,
        OTHER
    }

    public enum ScoreInterval {
        ZERO(0.0),
        LOW(0.2),
        MEDIUM(0.5),
        HIGH(0.7),
        VERY_HIGH(0.9),
        MAX(1.0);

        private final double value;

        ScoreInterval(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }

        public static ScoreInterval fromScore(double score) {
            if (score == 0.0) return ZERO;
            if (score == 0.2) return LOW;
            if (score == 0.5) return MEDIUM;
            if (score == 0.7) return HIGH;
            if (score == 0.9) return VERY_HIGH;
            if (score == 1.0) return MAX;
            return MEDIUM;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "animal_id")
    private Long animalId;

    @Column(unique = true)
    private String desertionNo;

    private AnimalType animalType;

    private String species;

    private double weight;

    private Integer animal_age;

    private String persona;

    @Enumerated(EnumType.STRING)
    private Gender animal_sex;

    private boolean isVaccinated;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private boolean isAdopted;

    private LocalDateTime createdAt;

    private String happenPlace;

    private String careNm;

    private String careTel;

    private String careAddr;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AnimalImage> images = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ScoreInterval people_friendly;

    @Enumerated(EnumType.STRING)
    private ScoreInterval active_playful;

    @Enumerated(EnumType.STRING)
    private ScoreInterval calm_quiet;

    @Enumerated(EnumType.STRING)
    private ScoreInterval adaptable;

    @Enumerated(EnumType.STRING)
    private ScoreInterval outdoor_activity;

    @Enumerated(EnumType.STRING)
    private ScoreInterval animal_friendly;

    @Enumerated(EnumType.STRING)
    private ScoreInterval beginner_possible;

    @Enumerated(EnumType.STRING)
    private ScoreInterval family_friendly;

    @Enumerated(EnumType.STRING)
    private ScoreInterval slow_bonding_ok;
}
