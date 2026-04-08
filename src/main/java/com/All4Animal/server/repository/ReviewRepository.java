package com.All4Animal.server.repository;

import com.All4Animal.server.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r join fetch r.user") // fetch join으로 처리한다.
    List<Review> findAllWithUser();
}
