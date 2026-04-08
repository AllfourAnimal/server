package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ReviewDetailResponse {
    private Long reviewId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

}
