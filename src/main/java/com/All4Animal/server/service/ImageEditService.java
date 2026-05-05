package com.All4Animal.server.service;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.AnimalImage;
import com.All4Animal.server.repository.AnimalImageRepository;
import com.All4Animal.server.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ImageEditService {

    private final RestClient restClient;
    private final S3Service s3Service;
    private final AnimalRepository animalRepository;
    private final AnimalImageRepository animalImageRepository;

    private static final String PROMPT = """
            유기동물의 실제 외형, 털색, 얼굴 특징은 유지하고 배경만 깔끔하게 정돈해줘.
            과하게 꾸미지 말고, 실제 동물의 레이아웃 유지하면서 사진처럼 자연스럽게 만들어줘.
            """;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    public ImageEditService(
            AnimalRepository animalRepository,
            AnimalImageRepository animalImageRepository,
            S3Service s3Service) {
        this.animalRepository = animalRepository;
        this.animalImageRepository = animalImageRepository;
        this.s3Service = s3Service;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .build();
    }

    // 모든 ai 이미지 생성
    public void createAllAiImage() {
        List<Animal> allAnimals = animalRepository.findAll();


        for (Animal allAnimal : allAnimals) {
            if (allAnimal == null) {
                continue;
            }

            if (StringUtils.hasText(allAnimal.getDesertionNo())
                    && allAnimal.getDesertionNo().startsWith("SEOUL_")) {
                System.out.println(allAnimal.getAnimalId() + "번 서울 데이터는 AI 이미지 생성을 스킵합니다.");
                continue;
            }

            try {
                generateEditedImageBase64(allAnimal);
                Thread.sleep(200);
            } catch (Exception e) {
                System.err.println(allAnimal.getAnimalId() + "번 Ai 이미지 생성 중 에러 발생" + e.getMessage());
            }
        }

    }

    // ai 이미지 만들고 저장
    @Transactional
    public String generateEditedImageBase64(Animal animal) {
        List<AnimalImage> animalImages =
                animalImageRepository.findByAnimal_AnimalId(animal.getAnimalId());

        boolean alreadyHasAiImage = animalImages.stream()
                .anyMatch(AnimalImage::isAiImage);

        if (alreadyHasAiImage) {
            System.out.println(animal.getAnimalId() + "번 동물은 이미 AI 이미지가 있어 스킵합니다.");
            return null;
        }

        AnimalImage originalImage =
                animalImageRepository.findFirstByAnimal_AnimalIdAndIsAiImageFalseOrderByImageIdAsc(
                        animal.getAnimalId()
                );

        if (originalImage == null) {
            throw new IllegalArgumentException("원본 이미지가 없습니다.");
        }

        if (!StringUtils.hasText(originalImage.getImageUrl())) {
            throw new IllegalArgumentException("이미지 URL이 비어 있습니다.");
        }

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-image-2",
                "images", List.of(
                        Map.of("image_url", originalImage.getImageUrl())
                ),
                "prompt", PROMPT,
                "size", "1024x1024",
                "quality", "medium",
                "output_format", "jpeg"
        );

        Map response = restClient.post()
                .uri("/images/edits")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        String aiImageBase64 = extractBase64Image(response);

        byte[] aiImageBytes = Base64.getDecoder().decode(aiImageBase64);

        String s3Key = s3Service.uploadAiAnimalImage(
                animal.getAnimalId(),
                aiImageBytes
        );

        AnimalImage animalImage = AnimalImage.builder()
                .animal(animal)
                .isAiImage(true)
                .imageUrl(s3Key)
                .build();

        animalImageRepository.save(animalImage);

        return aiImageBase64;
    }

    private String extractBase64Image(Map response) {
        if (response == null) {
            throw new IllegalStateException("OpenAI 이미지 응답이 비어 있습니다.");
        }

        Object dataObject = response.get("data");
        if (!(dataObject instanceof List<?> data) || data.isEmpty()) {
            throw new IllegalStateException("OpenAI 이미지 응답에 data가 없습니다.");
        }

        Object firstObject = data.get(0);
        if (!(firstObject instanceof Map<?, ?> firstImage)) {
            throw new IllegalStateException("OpenAI 이미지 응답 형식이 올바르지 않습니다.");
        }

        Object base64Object = firstImage.get("b64_json");
        if (!(base64Object instanceof String base64Image) || !StringUtils.hasText(base64Image)) {
            throw new IllegalStateException("OpenAI 이미지 응답에 b64_json이 없습니다.");
        }

        return base64Image;
    }
}

