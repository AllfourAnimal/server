package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {

    boolean existsByDesertionNo(String desertionNo);

    List<Animal> findAllByOrderByCreatedAtDesc();

    void deleteByDesertionNoNotIn(List<String> animals);

    @Query("""
          SELECT a
          FROM Animal a
          WHERE
              (:keyword IS NULL OR :keyword = ''
                  OR a.species LIKE %:keyword%
                  OR a.description LIKE %:keyword%
                  OR a.happenPlace LIKE %:keyword%
                  OR a.careNm LIKE %:keyword%)
              AND (:region IS NULL OR :region = ''
                  OR a.careAddr LIKE %:region%)
              AND (:animalType IS NULL OR a.animalType = :animalType)
          ORDER BY a.createdAt DESC
      """)
    List<Animal> searchAnimals(
            @Param("keyword") String keyword,
            @Param("region") String region,
            @Param("animalType") Animal.AnimalType animalType
    );
}
