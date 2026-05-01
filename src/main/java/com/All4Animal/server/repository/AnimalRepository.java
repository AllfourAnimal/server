package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long>, JpaSpecificationExecutor<Animal> {
    boolean existsByDesertionNo(String desertionNo);
    List<Animal> findAllByOrderByCreatedAtDesc();
    List<Animal> findByCareAddrStartingWithOrCareAddrStartingWithOrCareAddrStartingWithOrderByCreatedAtDesc(
            String seoul, String gyeonggi, String incheon
    );
}
