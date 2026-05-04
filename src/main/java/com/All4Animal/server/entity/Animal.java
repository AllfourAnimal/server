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
}
