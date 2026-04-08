package com.All4Animal.server.service;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Story;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.StoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoryService {
    private final StoryRepository storyRepository;
    private final AnimalRepository animalRepository;

    @Transactional
    public String createAndSaveStory(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        String prompt = String.format(
                "이 유기동물의 정보를 바탕으로 감동적인 스토리를 3줄 정도로 짧게 작성해줘. " +
                        "종 : %s, 특징 : %s, 발견장소 : %s",
                animal.getSpecies(), animal.getDescription(), animal.getHappenPlace()
        );

        String generatedContent = animal.getHappenPlace();

        Story stroy = Story.builder()
                .animal(animal)
                .storyContent(generatedContent)
                .build();

        storyRepository.save(stroy);
        return generatedContent;
    }
}
