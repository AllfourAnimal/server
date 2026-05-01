package com.All4Animal.server.service;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Story;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.StoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final AnimalRepository animalRepository;
    private final ChatClient chatClient;

    public StoryService(StoryRepository storyRepository,
                        AnimalRepository animalRepository,
                        ChatClient.Builder builder) {
        this.storyRepository = storyRepository;
        this.animalRepository = animalRepository;
        this.chatClient = builder.build();
    }

    public String generatePrompt(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        StringBuilder prompt = new StringBuilder();
        prompt.append("너는 유기동물 보호소의 홍보 담당자야. " +
                "아래 제공되는 동물의 정보를 바탕으로, 이 아이가 새로운 가족을 기다리며 꾸는 꿈이나" +
                "일상을 담은 3문장 내외의 따뜻하고 감성적인 스토리를 1인칭 시점(동물 시점)으로 작성해줘." +
                "마지막엔 '#사지말고입양하세요' 해시태그를 붙여줘.");

        prompt.append(String.format("이 유기 동물의 정보를 바탕으로 감동적인 스토리 2~3줄 정도로 짧게 작성해줘." +
                "종 : %s, 특징 : %s, 발견 장소 : %s",
                animal.getSpecies(),
                animal.getDescription(),
                animal.getHappenPlace()));

        return prompt.toString();
    }

    public String generateStory(String prompt) {
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }

    @Transactional
    public String createAndSaveStory(Long animalId) {
        String prompt = generatePrompt(animalId);

        String generatedContent = generateStory(prompt);

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        Story story = Story.builder()
                .animal(animal)
                .storyContent(generatedContent)
                .build();

        storyRepository.save(story);

        return generatedContent;
    }
    
    @Transactional
    public void createStoryAll() {
        List<Animal> allAnimals = animalRepository.findAll();

        for (Animal animal : allAnimals) {
            if(!storyRepository.existsByAnimal_AnimalId(animal.getAnimalId())) {
                try {
                    createAndSaveStory(animal.getAnimalId());
                    Thread.sleep(200);
                } catch (Exception e) {
                    System.err.println(animal.getAnimalId() + "번 스토리 생성 중 에러 : " + e.getMessage());
                }
            }
        }
    }
}
