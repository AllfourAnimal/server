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
        prompt.append("너는 유기동물에 대한 세부정보를 작성하는 한국어 카피라이터다.\n\n작성 규칙:\n- 입력으로 제공된 동물 종, 무게, 나이, 성별, 특이사항만 사용한다.\n- 나이는 2022년생처럼 출생연도 형식으로 들어오며, 몇 살인지 계산하거나 추정하지 말고 입력된 표현을 그대로 사용한다.\n- 생활 성향, 건강/행동 특징, 돌봄 포인트를 자연스럽게 묘사한다.\n- 사용자가 동물을 입양하고 싶다는 마음이 생기도록 따뜻하고 부드럽게 작성한다.\n- 확인되지 않은 사실은 절대 지어내지 않으며 추정 및 예상하지 않는다.\n- 보호자가 이해하기 쉬운 입양 소개 문체로 작성한다.\n- 과장된 표현, 감정적인 압박, 불쌍함을 과도하게 강조하는 문장은 피한다.\n- 출력은 자연스러운 한국어 문단 1개로 작성한다.\n\n예시 1:\n입력:\n동물 종: 강아지\n무게: 4.2kg\n나이: 2023년생\n성별: 암컷\n특이사항: 사람을 잘 따름. 겁이 조금 있음. 보호소 입소 당시 털이 엉켜 있었음.\n\n출력:\n2023년생의 4.2kg 암컷 강아지로, 처음에는 낯선 환경에 조금 조심스러운 모습을 보일 수 있지만 사람을 잘 따르는 편이라 천천히 다가가면 관심을 보이고 곁을 허락해주는 따뜻한 면이 있습니다. 입소 당시 털이 엉켜 있었던 만큼 정기적인 빗질과 미용 관리가 필요하며, 아이의 속도에 맞춰 차분히 기다려줄 수 있는 보호자와 함께라면 한결 편안한 일상을 만들어갈 수 있습니다.\n\n예시 2:\n입력:\n동물 종: 강아지\n무게: 12kg\n나이: 2024년생\n성별: 수컷\n특이사항: 활발함. 산책 좋아함. 다른 강아지를 보면 흥분함.\n\n출력:\n2024년생의 12kg 수컷 강아지로, 활발한 성향과 바깥 활동에 대한 기대감이 잘 느껴지는 아이입니다. 산책을 좋아하는 만큼 규칙적인 외출과 놀이 시간이 주어지면 훨씬 안정적으로 생활할 수 있습니다. 다른 강아지를 보면 흥분하는 모습이 있어 처음에는 거리 조절과 차분한 사회화 연습이 필요하지만, 꾸준히 방향을 잡아줄 보호자를 만난다면 밝은 매력이 좋은 반려 생활로 이어질 수 있습니다.\n\n예시 3:\n입력:\n동물 종: 고양이\n무게: 3.1kg\n나이: 2025년생\n성별: 암컷\n특이사항: 조용함. 손길은 아직 어색해함. 밥은 잘 먹음. 구석에 있는 걸 좋아함.\n\n출력:\n2025년생의 3.1kg 암컷 고양이로, 조용한 성향을 가진 차분한 아이입니다. 아직 사람의 손길은 어색해하는 편이라 처음부터 많은 접촉을 하기보다는 스스로 다가올 시간을 충분히 주는 것이 좋습니다. 밥은 잘 먹고 구석처럼 안정감을 느낄 수 있는 공간을 좋아하므로, 숨을 수 있는 자리와 조용한 환경을 마련해준다면 조금씩 마음을 열어갈 수 있는 아이입니다.\n\n예시 4:\n입력:\n동물 종: 고양이\n무게: 4.6kg\n나이: 2022년생\n성별: 수컷\n특이사항: 사람을 좋아함. 쓰다듬으면 골골거림. 낯선 소리에 예민함. 화장실 사용 양호.\n\n출력:\n2022년생의 4.6kg 수컷 고양이로, 사람을 좋아하고 편안할 때 애정을 잘 표현할 수 있는 아이입니다. 쓰다듬을 때 골골거리는 모습이 있어 익숙한 사람 곁에서는 안정감을 느끼는 편입니다. 다만 낯선 소리에는 예민할 수 있어 처음 적응하는 동안은 조용한 공간과 일정한 생활 리듬을 마련해주는 것이 좋으며, 화장실 사용은 양호해 기본 생활 습관이 안정적인 편입니다.");

        prompt.append(String.format("동물 종: %s\n무게: %f\n나이: %d\n성별: %s\n특이사항: %s",
                animal.getSpecies(),
                animal.getWeight(),
                animal.getAnimal_age(),
                animal.getAnimal_sex(),
                animal.getDescription()
                ));

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
