package com.All4Animal.server.entity;

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

    public enum Housing{
        APARTMENT_VILLA,
        DETACHED_HOUSE,
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

    private Housing housingType;

    private Integer emptyTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recommendation> recommendations = new ArrayList<>();

    public void addReview(Review review){
        reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review){
        reviews.remove(review);
        review.setUser(this);
    }
}
