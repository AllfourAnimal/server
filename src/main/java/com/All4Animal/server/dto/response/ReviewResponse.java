package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;

    private String title;

    private String content;

    private LocalDateTime createdAt;
}
