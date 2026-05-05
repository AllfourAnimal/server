package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewDetailDto {

    private Long reviewId;
    private String title;
    private String content;
    private String desertionNo;
    private String happenPlace;
    private LocalDateTime createdAt;
    private String imageKey;
}
