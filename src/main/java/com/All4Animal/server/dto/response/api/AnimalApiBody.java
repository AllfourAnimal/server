package com.All4Animal.server.dto.response.api;

import lombok.Data;

import java.util.List;

@Data
public class AnimalApiBody {
    private List<AnimalApiResponse> items;
}
