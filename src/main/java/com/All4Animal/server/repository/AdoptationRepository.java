package com.All4Animal.server.repository;


import com.All4Animal.server.entity.Adoptation;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdoptationRepository extends JpaRepository<Adoptation, Long> {

    Optional<Adoptation> findByUserAndAnimal(Users user, Animal animal);

    @Query("""
            SELECT ad
            FROM Adoptation ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            WHERE ad.user = :user
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoptation> findAllByUserWithAnimalOrderByUpdatedAtDesc(@Param("user") Users user);

    @Query("""
            SELECT ad
            FROM Adoptation ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            WHERE ad.status = :status
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoptation> findAllByStatusWithAnimalOrderByUpdatedAtDesc(@Param("status") Adoptation.AdoptionStatus status);

    @Query("""
            SELECT ad
            FROM Adoptation ad
            JOIN FETCH ad.user
            JOIN FETCH ad.animal
            ORDER BY ad.updatedAt DESC
            """)
    List<Adoptation> findAllWithAnimalOrderByUpdatedAtDesc();
}
