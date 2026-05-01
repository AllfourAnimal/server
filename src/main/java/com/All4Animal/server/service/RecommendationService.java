package com.All4Animal.server.service;

import com.All4Animal.server.dto.response.AnimalFilterResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Recommendation;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final AnimalRepository animalRepository;

//    public AnimalFilterResponse getAnimalFilter(Long userId){
//        List<Animal> animals = animalRepository.
//    }
}
