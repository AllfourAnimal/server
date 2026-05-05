package com.All4Animal.server.repository;


import com.All4Animal.server.entity.Adoption;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

    Optional<Adoption> findByUserAndAnimal(Users user, Animal animal);

    boolean existsByUserAndAnimalAndStatus(Users user, Animal animal, Adoption.AdoptionStatus status);

    boolean existsByUserAndAnimalAndStatusIn(Users user, Animal animal, List<Adoption.AdoptionStatus> statuses);

    @Query("""
            SELECT ad
            FROM Adoption ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            WHERE ad.user.userId = :userId
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoption> findAllByUserIdWithAnimalOrderByUpdatedAtDesc(@Param("userId") Long userId);

    @Query("""
            SELECT ad
            FROM Adoption ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            WHERE ad.status = :status
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoption> findAllByStatusWithAnimalOrderByUpdatedAtDesc(@Param("status") Adoption.AdoptionStatus status);

    @Query("""
            SELECT ad
            FROM Adoption ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoption> findAllWithAnimalOrderByUpdatedAtDesc();
}
