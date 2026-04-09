package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Long> {
    boolean existsByAnimal_AnimalId(Long animalId);

    Optional<Story> findByAnimal(Animal animal);
}
