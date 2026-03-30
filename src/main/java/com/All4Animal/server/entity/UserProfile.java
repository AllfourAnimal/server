package com.All4Animal.server.entity;

import com.All4Animal.server.dto.request.UserProfileRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserProfile {
    public enum HousingType {
        ONE_ROOM, APARTMENT, HOUSE
    }

    public enum ActivityLevel {
        LOW, MEDIUM, HIGH
    }

    public enum PetExperience {
        NONE, LITTLE, MUCH
    }

    public enum PreferredSpecies {
        DOG, CAT, ANY
    }

    public enum PreferredSize {
        SMALL, MEDIUM, LARGE, ANY
    }

    public enum PreferredAgeGroup {
        BABY, ADULT, SENIOR, ANY
    }

    public enum NoiseTolerance {
        LOW, MEDIUM, HIGH
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private Users users;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private HousingType housingType;

    @Column(nullable = false)
    private Boolean hasChildren;

    @Column(nullable = false)
    private Boolean hasElderly;

    @Column(nullable = false)
    private Boolean hasOtherPets;

    @Column(nullable = false)
    private Integer absenceHours;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private PetExperience petExperience;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private PreferredSpecies preferredSpecies;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private PreferredSize preferredSize;

    @Enumerated(EnumType.STRING)
    @Column(length = 12, nullable = false)
    private PreferredAgeGroup preferredAgeGroup;

    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private NoiseTolerance noiseTolerance;

    @Column(length = 255)
    private String allergyInfo;

    @Column(length = 500)
    private String specialNote;

    private Boolean profileCompleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void updateProfile(Users user, UserProfileRequest request, LocalDateTime now) {
        this.users = user;
        this.housingType = request.getHousingType();
        this.hasChildren = request.getHasChildren();
        this.hasElderly = request.getHasElderly();
        this.hasOtherPets = request.getHasOtherPets();
        this.absenceHours = request.getAbsenceHours();
        this.activityLevel = request.getActivityLevel();
        this.petExperience = request.getPetExperience();
        this.preferredSpecies = request.getPreferredSpecies();
        this.preferredSize = request.getPreferredSize();
        this.preferredAgeGroup = request.getPreferredAgeGroup();
        this.noiseTolerance = request.getNoiseTolerance();
        this.allergyInfo = request.getAllergyInfo();
        this.specialNote = request.getSpecialNote();
        this.profileCompleted = true;
        this.updatedAt = now;
    }
}


