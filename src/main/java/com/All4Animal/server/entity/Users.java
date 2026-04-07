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
    @Column(name = "user_id", updatable = false)
    private Long userId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;
    @Column(name = "password", nullable = false)
    private String password;

    private LocalDateTime createdAt;
    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "is_experience", nullable = false)
    private boolean isExperience;

    private Housing housingType;

    private Integer emptyTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public void addReview(Review review){
        reviews.add(review);
        review.setUser(this);
    }

    public void removeReview(Review review){
        reviews.remove(review);
        review.setUser(this);
    }
}
