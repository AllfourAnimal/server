package com.All4Animal.server.repository;

import com.All4Animal.server.entity.AnimalImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimalImageRepository extends JpaRepository<AnimalImage, Long> {
    List<AnimalImage> findByAnimal_AnimalId(Long animalId);
    AnimalImage findFirstByAnimal_AnimalIdAndIsAiImageFalseOrderByImageIdAsc(Long animalId);
}
