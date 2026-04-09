package com.All4Animal.server.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ReviewRequest {

    private String title;
    private String content;
    private Long animalId;
    private String animalUrl;
}
