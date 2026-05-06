package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewDetailDto {

    private Long reviewId;
    private String title;
    private String petName;
    private String content;
    private Long userId;
    private String username;
    private String desertionNo;
    private String happenPlace;
    private String species;
    private boolean isAdopted;
    private LocalDateTime adoptedAt;
    private LocalDateTime createdAt;
    private String imageKey;
}
