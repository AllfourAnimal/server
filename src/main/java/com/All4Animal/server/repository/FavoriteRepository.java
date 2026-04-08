package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Favorite;
import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndAnimal(Users user, Animal animal);

    Optional<Favorite> findByUserAndAnimal(Users user, Animal animal);
}
