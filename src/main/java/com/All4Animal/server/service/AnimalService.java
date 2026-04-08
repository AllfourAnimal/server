package com.All4Animal.server.service;

import com.All4Animal.server.client.AnimalApiClient;
import com.All4Animal.server.dto.response.api.AnimalApiResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalApiClient animalApiClient;

    public List<Animal> getAllAnimals() {
        return animalRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void syncAnimalsWithApi() {
        List<AnimalApiResponse> apiItems = animalApiClient.fetchAnimals();

        if (apiItems == null || apiItems.isEmpty()) {
            System.out.println("불러온 데이터 없음");
        } else {
            System.out.println("불러온 데이터 개수: " + apiItems.size() + "개");
            saveApiAnimals(apiItems);
        }
    }

    @Transactional
    public void saveApiAnimals(List<AnimalApiResponse> apiItems) {
        for(AnimalApiResponse item : apiItems) {
            if (animalRepository.existsByDesertionNo(item.getDesertionNo())) {
                System.out.println(item.getDesertionNo());
                continue;
            }

            Animal animal = convertToEntity(item);
            animalRepository.save(animal);
        }
    }

    private Animal convertToEntity(AnimalApiResponse dto) {
        return Animal.builder()
                .desertionNo(dto.getDesertionNo())
                .species(dto.getKindNm())
                .weight(parseWeight(dto.getWeight()))
                .animal_age(parseAge(dto.getAge()))
                .animal_sex(mapGender(dto.getSexCd(), dto.getNeuterYn()))
                .description(dto.getSpecialMark())
                .happenPlace(dto.getHappenPlace())
                .careNm(dto.getCareNm())
                .careTel(dto.getCareTel())
                .careAddr(dto.getCareAddr())
                .isAdopted(dto.getProcessState().contains("종료"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private double parseWeight(String weightStr) {
        try {
            String numeric = weightStr.replaceAll("[^0-9.]", "");
            return Double.parseDouble(numeric);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseAge(String ageStr) {
        try {
            String numeric = ageStr.replaceAll("[^0-9]", "");
            return Integer.parseInt(numeric);
        } catch (Exception e) {
            return null;
        }
    }

    private Animal.Gender mapGender(String sexCd, String neuterYn) {
        if("Y".equals(neuterYn))
            return Animal.Gender.NEUTERED;

        if("F".equals(sexCd))
            return Animal.Gender.FEMALE;

        return Animal.Gender.MALE;
    }
}
