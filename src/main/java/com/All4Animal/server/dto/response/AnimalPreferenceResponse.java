package com.All4Animal.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AnimalPreferenceResponse {
    private String animalType;
    private String size;
    private String gender;
    private String age;
    private String personalities;
    private boolean completed;
    private LocalDateTime updatedAt;
}
