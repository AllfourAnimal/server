package com.All4Animal.server.service;

import com.All4Animal.server.client.AnimalApiClient;
import com.All4Animal.server.dto.request.AnimalSearchRequest;
import com.All4Animal.server.dto.response.AnimalResponse;
import com.All4Animal.server.dto.response.AnimalSearchResponse;
import com.All4Animal.server.dto.response.api.AnimalApiResponse;
import com.All4Animal.server.dto.response.api.SeoulAnimalApiResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.AnimalImage;
import com.All4Animal.server.repository.AnimalImageRepository;
import com.All4Animal.server.repository.AnimalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.All4Animal.server.dto.response.api.SeoulAnimalImageApiResponse;


import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final AnimalImageRepository animalImageRepository;
    private final AnimalApiClient animalApiClient;

    public List<AnimalImage> getImageByAnimalId(Long animalId) {
        return animalImageRepository.findByAnimal_AnimalId(animalId);
    }

    // 동물 조회 페이징 기능
    public List<AnimalResponse> getAnimalsWithPaging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return animalRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(AnimalResponse::from)
                .toList();
    }

    // 동물 검색 필터 기능
    public List<AnimalSearchResponse> searchAnimals(AnimalSearchRequest request) {
        return animalRepository.searchAnimals(
                        request.getKeyword(),
                        request.getCareAddr(),
                        request.getAnimalType()
                ).stream()
                .map(AnimalSearchResponse::from)
                .toList();
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void syncAnimalsWithApi() {
        List<AnimalApiResponse> nationalItems = animalApiClient.fetchNationalAnimals();
        List<SeoulAnimalApiResponse> seoulItems = animalApiClient.fetchSeoulAnimals();
        List<SeoulAnimalImageApiResponse> seoulImages = animalApiClient.fetchSeoulAnimalImages();

        int totalFetched = (nationalItems != null ? nationalItems.size() : 0)
                + (seoulItems != null ? seoulItems.size() : 0);

        if (totalFetched == 0) {
            System.out.println("불러온 데이터 없음");
            return;
        }

        System.out.println("불러온 총 데이터 개수: " + totalFetched + "개");

        saveAllApiAnimals(nationalItems, seoulItems, seoulImages);
    }

    @Transactional
    public void saveAllApiAnimals(
            List<AnimalApiResponse> nationalItems,
            List<SeoulAnimalApiResponse> seoulItems,
            List<SeoulAnimalImageApiResponse> seoulImages) {
        List<String> currentApiIds = new ArrayList<>();

        if (nationalItems != null) {
            currentApiIds.addAll(nationalItems.stream().map(AnimalApiResponse::getDesertionNo).toList());
        }
        if (seoulItems != null) {
            currentApiIds.addAll(
                    seoulItems.stream()
                            .filter(item -> {
                                String adoptStatus = item.getAdoptStatus();
                                return adoptStatus == null || !adoptStatus.contains("완료");
                            })
                            .map(item -> "SEOUL_" + normalizeSeq(item.getSeq()))
                            .toList()
            );
        }

        // 사라진 데이터 삭제
        if (nationalItems != null && !nationalItems.isEmpty() && seoulItems != null && !seoulItems.isEmpty()) {
            animalRepository.deleteByDesertionNoNotIn(currentApiIds);
        }

        // 공공 데이터 처리
        if (nationalItems != null) {
            for (AnimalApiResponse item : nationalItems) {

                if (animalRepository.existsByDesertionNo(item.getDesertionNo()))
                    continue;

                if (!"보호중".equals(item.getProcessState()))
                    continue;

                Animal animal = convertToEntity(item);
                processImages(animal, item.getPopfile1(), item.getPopfile2());
                animalRepository.save(animal);
            }
        }

        Map<String, String> seoulImageMap = new HashMap<>();

        if (seoulImages != null) {
            seoulImageMap = seoulImages.stream()
                    .filter(img -> img.getSeq() != null)
                    .filter(img -> img.getImgUrl() != null && !img.getImgUrl().isBlank())
                    .filter(img -> "THUMB".equals(img.getImgType()))
                    .collect(Collectors.toMap(
                            img -> normalizeSeq(img.getSeq()),
                            SeoulAnimalImageApiResponse::getImgUrl,
                            (first, second) -> first
                    ));
        }

        // 서울시 데이터 처리
        if (seoulItems != null) {
            for (SeoulAnimalApiResponse item : seoulItems) {
                String seq = normalizeSeq(item.getSeq());
                String seoulId = "SEOUL_" + seq;
                String adoptStatus = item.getAdoptStatus();

                if (animalRepository.existsByDesertionNo(seoulId))
                    continue;
                if (adoptStatus != null && adoptStatus.contains("완료"))
                    continue;

                String seoulImageUrl = seoulImageMap.get(seq);

                Animal animal = convertSeoulToEntity(item, seoulId);
                processImages(animal, seoulImageUrl, null);
                animalRepository.save(animal);
            }
        }
    }

    private String normalizeSeq(String seq) {
        if (seq == null) return null;
        return seq.replace(".0", "");
    }

    private Animal convertSeoulToEntity(SeoulAnimalApiResponse dto, String seoulId) {
        return Animal.builder()
                .desertionNo(seoulId)
                .animalType(mapSeoulAnimalType(dto.getAnimalType()))
                .species(dto.getAnimalBreed())
                .weight(dto.getWeightKg() != null ? dto.getWeightKg() : 0.0)
                .animal_age(parseSeoulBirthYear(dto.getAnimalBirthYmd()))
                .animal_sex("W".equals(dto.getAnimalSex()) ? Animal.Gender.FEMALE : Animal.Gender.MALE)
                .description(stripHtml(dto.getCont())) // HTML 태그 제거 로직 추가 추천
                .happenPlace("미상")
                .careNm("서울동물복지지원센터")
                .careAddr("서울특별시")
                .isAdopted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Integer parseSeoulBirthYear(String birthYmd) {
        Integer parsed = parseAge(birthYmd);
        if (parsed == null) return null;
        return parsed / 100;
    }


    private void processImages(Animal animal, String img1, String img2) {
        if (img1 != null && !img1.isEmpty()) {
            animal.getImages().add(AnimalImage.builder().imageUrl(img1).animal(animal).createdAt(LocalDateTime.now()).build());
        }
        if (img2 != null && !img2.isEmpty()) {
            animal.getImages().add(AnimalImage.builder().imageUrl(img2).animal(animal).createdAt(LocalDateTime.now()).build());
        }
    }

    private Animal.AnimalType mapSeoulAnimalType(String type) {
        if ("DOG".equals(type)) return Animal.AnimalType.DOG;
        if ("CAT".equals(type)) return Animal.AnimalType.CAT;
        return Animal.AnimalType.OTHER;
    }

    private String stripHtml(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
    }

    // api를 Entity 형식에 맞게 매핑
    private Animal convertToEntity(AnimalApiResponse dto) {
        Animal.AnimalType type = convertToAnimalType(dto); // 개, 고양이 매핑


        return Animal.builder()
                .desertionNo(dto.getDesertionNo())
                .animalType(type)
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

            if(Integer.parseInt(numeric) > 100000)
                numeric = String.valueOf(Integer.parseInt(numeric) / 100);

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

    private Animal.AnimalType convertToAnimalType(AnimalApiResponse dto) {
        String upKind = dto.getUpKindNm();

        if("개".equals(upKind))
            return Animal.AnimalType.DOG;

        if("고양이".equals(upKind))
            return Animal.AnimalType.CAT;


        return Animal.AnimalType.OTHER;
    }
}
