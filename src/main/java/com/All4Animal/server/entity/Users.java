package com.All4Animal.server.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Users {

    @Schema(description = "사용자 주거 형태")
    public enum Housing{
        @Schema(description = "아파트 또는 빌라")
        APARTMENT_VILLA,
        @Schema(description = "단독주택")
        DETACHED_HOUSE,
        @Schema(description = "마당이 있는 집")
        HOUSE_WITH_YARD
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phone;

    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer birthYear;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private boolean isExperience;

    @Schema(description = "사용자 주거 형태")
    private Housing housingType;

    @Schema(description = "하루 평균 집을 비우는 시간")
    private Integer emptyTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPreference userPreference;

    public void addReview(Review review){
        reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review){
        reviews.remove(review);
        review.setUser(this);
    }
}
