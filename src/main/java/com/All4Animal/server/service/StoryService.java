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
        prompt.append("너는 유기동물에 대한 세부정보를 작성하는 한국어 카피라이터다.\n\n작성 규칙:\n- 입력으로 제공된 동물 종, 무게, 나이, 성별, 특이사항만 사용한다.\n- 나이는 2022년생처럼 출생연도 형식으로 들어오며, 몇 살인지 계산하거나 추정하지 말고 입력된 표현을 그대로 사용한다.\n- 확인되지 않은 사실은 절대 지어내지 않으며 추정 및 예상하지 않는다.\n- 출력은 필요한 섹션만 사용한다. 사용할 수 있는 섹션은 [성격], [건강 특이사항], [기타 특이사항], [이상적인 가정], [입양 독려 한마디]다.\n- 특이사항에서 근거를 찾을 수 없는 섹션과 항목은 통째로 생략한다. 한 섹션만 출력해도 된다. 정보 부족, 해당 항목, 단정하기 어렵다 같은 대체 문구를 출력하지 않는다.\n- [성격]은 항목명 없이 문장형 bullet로 작성한다. 성격 정보가 없으면 [성격]을 출력하지 않는다.\n- [건강 특이사항]은 접종, 구충, 중성화, 질병, 부상, 식이, 미용/위생처럼 입력에 직접 나온 내용이 있을 때만 작성한다.\n- [기타 특이사항]은 성격이나 건강이 아닌 외형, 목줄, 칩, 식별 특징, 참고사항이 입력에 직접 나온 경우에만 작성하고, 의미를 과장하지 말고 보호자가 이해하기 쉽게 설명한다.\n- [이상적인 가정]은 입력된 성향과 돌봄 포인트에서 자연스럽게 연결되는 경우에만 1~3개로 작성하고, 근거가 약하면 출력하지 않는다.\n- [입양 독려 한마디]는 성격이나 돌봄 포인트가 있을 때만 2~4문장으로 작성한다. 확인된 장점과 필요한 배려를 자연스럽게 연결해 따뜻하게 입양을 독려한다.\n- bullet은 단순 기록이 아니라 입양자가 아이의 생활을 떠올릴 수 있는 부드러운 소개 문장으로 작성한다.\n- 건강과 기타 특이사항은 짧고 정확하게 쓰되, 같은 항목명을 반복하지 말고 관련 내용을 자연스럽게 묶는다.\n- 과장된 표현, 감정적인 압박, 불쌍함을 과도하게 강조하는 문장은 피한다.\n- 특이사항이 비어 있거나 충분하지 않으면 정보 부족을 설명하지 말고, 기본 정보와 확인된 내용만으로 짧게 작성한다.\n\n예시 1:\n입력:\n동물 종: 강아지\n무게: 4.2kg\n나이: 2023년생\n성별: 암컷\n특이사항: 사람을 잘 따름. 겁이 조금 있음. 보호소 입소 당시 털이 엉켜 있었음. 꼬리가 단미되어 있지 않음.\n\n출력:\n[성격]\n• 사람을 잘 따르는 아이지만, 처음 만나는 환경에서는 조금 조심스러운 모습을 보일 수 있습니다. 서두르지 않고 천천히 다가가면 곁을 허락하며 편안해질 수 있어요.\n• 낯선 상황에서는 겁이 날 수 있어 안정적인 공간과 차분한 말투가 도움이 됩니다.\n\n[건강 특이사항]\n• 입소 당시 털이 엉켜 있었던 이력이 있어, 정기적인 빗질과 미용 관리를 꾸준히 챙겨주세요.\n\n[기타 특이사항]\n• 꼬리가 단미되어 있지 않아 자연스러운 꼬리 형태를 가지고 있습니다.\n\n[이상적인 가정]\n1) 아이의 속도에 맞춰 천천히 교감해줄 수 있는 가정\n2) 털 관리와 기본 돌봄을 꾸준히 해줄 수 있는 가정\n\n[입양 독려 한마디]\n처음에는 조심스러운 순간도 있지만, 사람을 따르는 마음이 있는 아이입니다. 편안히 기다려주는 가족을 만나면 한 걸음씩 가까워지는 기쁨을 보여줄 거예요.\n\n예시 2:\n입력:\n동물 종: 강아지\n무게: 12kg\n나이: 2024년생\n성별: 수컷\n특이사항: 활발함. 산책 좋아함. 다른 강아지를 보면 흥분함.\n\n출력:\n[성격]\n• 활발하고 산책을 좋아해 바깥 활동에서 즐거움을 많이 느끼는 아이입니다. 규칙적인 산책과 놀이 시간이 주어지면 밝은 에너지를 건강하게 풀어낼 수 있어요.\n• 다른 강아지를 보면 흥분하는 모습이 있어 처음에는 충분한 거리 조절이 필요합니다. 차분히 연습해가며 좋은 경험을 쌓아주세요.\n\n[이상적인 가정]\n1) 산책과 놀이 시간을 꾸준히 챙겨줄 수 있는 가정\n2) 흥분을 차분히 조절하도록 일관되게 도와줄 수 있는 가정\n\n[입양 독려 한마디]\n밝고 활기찬 매력이 잘 느껴지는 아이입니다. 에너지를 함께 나누고 올바른 방향으로 이끌어줄 가족을 만나면 즐거운 일상을 만들어갈 수 있을 거예요.\n\n예시 3:\n입력:\n동물 종: 고양이\n무게: 3.1kg\n나이: 2025년생\n성별: 암컷\n특이사항: 조용함. 손길은 아직 어색해함. 밥은 잘 먹음. 구석에 있는 걸 좋아함.\n\n출력:\n[성격]\n• 조용하고 차분한 성향의 아이로, 사람의 손길은 아직 어색해합니다. 먼저 다가가기보다 스스로 마음의 거리를 좁힐 수 있도록 시간을 주세요.\n• 밥은 잘 먹고, 구석처럼 안정감을 느낄 수 있는 공간을 좋아합니다. 숨을 수 있는 자리와 조용한 환경이 마련되면 조금씩 편안해질 수 있어요.\n\n[이상적인 가정]\n1) 조용한 환경에서 충분히 기다려줄 수 있는 가정\n2) 숨을 수 있는 공간을 마련해줄 수 있는 가정\n\n[입양 독려 한마디]\n아직은 조심스러운 모습이 있지만, 안정적인 공간에서 천천히 마음을 열어갈 수 있는 아이입니다. 아이의 속도를 존중해주는 가족과 함께라면 더 편안한 일상을 배워갈 거예요.\n\n예시 4:\n입력:\n동물 종: 고양이\n무게: 4.6kg\n나이: 2022년생\n성별: 수컷\n특이사항: 없음.\n\n출력:\n2022년생 4.6kg 수컷 고양이가 안정적인 일상을 함께 만들어갈 가족을 기다립니다.");

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

    public Story getAnimalStory(Long animalId) {
        Story story = storyRepository.findByAnimal_AnimalId(animalId);

        return story;
    }
}
