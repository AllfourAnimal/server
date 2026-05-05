package com.All4Animal.server.repository;

import com.All4Animal.server.dto.response.ReviewDetailDto;
import com.All4Animal.server.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("select r from Review r join fetch r.user") // fetch join으로 처리한다.
    List<Review> findAllWithUser();

    @Query("""
            SELECT new com.All4Animal.server.dto.response.ReviewDetailDto(
                r.reviewId,
                r.title,
                r.content,
                a.desertionNo,
                a.happenPlace,
                r.createdAt,
                r.imageKey
            )
            FROM Review r
            JOIN r.animal a
            WHERE r.reviewId = :reviewId
            """)
    List<ReviewDetailDto> findReviewDetailDtoById(@Param("reviewId") Long reviewId);
}
