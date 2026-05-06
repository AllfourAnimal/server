package com.All4Animal.server.repository;

import com.All4Animal.server.dto.response.ReviewDetailDto;
import com.All4Animal.server.entity.Adoption;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Review;
import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByUserAndAnimal(Users user, Animal animal);

    @Query("""
            SELECT r.animal.animalId
            FROM Review r
            WHERE r.user = :user
            """)
    List<Long> findReviewedAnimalIdsByUser(@Param("user") Users user);

    @Query("""
            SELECT r
            FROM Review r
            JOIN FETCH r.user
            JOIN FETCH r.animal
            ORDER BY r.createdAt DESC
            """)
    List<Review> findAllWithUser();

    @Query("""
            SELECT r
            FROM Review r
            JOIN FETCH r.user
            JOIN FETCH r.animal a
            WHERE a.animalType = :animalType
            ORDER BY r.createdAt DESC
            """)
    List<Review> findAllByAnimalTypeWithUserAndAnimal(@Param("animalType") Animal.AnimalType animalType);

    @Query("""
            SELECT r
            FROM Review r
            JOIN FETCH r.user
            JOIN FETCH r.animal a
            WHERE a.isAdopted = true
            ORDER BY r.createdAt DESC
            """)
    List<Review> findAllByAdoptedAnimalWithUserAndAnimal();

    @Query("""
            SELECT new com.All4Animal.server.dto.response.ReviewDetailDto(
                r.reviewId,
                r.title,
                r.petName,
                r.content,
                u.userId,
                u.username,
                a.desertionNo,
                a.happenPlace,
                a.species,
                a.isAdopted,
                ad.updatedAt,
                r.createdAt,
                r.imageKey
            )
            FROM Review r
            JOIN r.user u
            JOIN r.animal a
            LEFT JOIN Adoption ad
                ON ad.user = r.user
                AND ad.animal = r.animal
                AND ad.status = :completedStatus
            WHERE r.reviewId = :reviewId
            """)
    List<ReviewDetailDto> findReviewDetailDtoById(
            @Param("reviewId") Long reviewId,
            @Param("completedStatus") Adoption.AdoptionStatus completedStatus
    );
}
