package com.All4Animal.server.service;

import com.All4Animal.server.dto.response.AnimalFilterResponse;
import com.All4Animal.server.dto.response.RecommendedAnimalResponse;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.AnimalImage;
import com.All4Animal.server.entity.UserPreference;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.RecommendationRepository;
import com.All4Animal.server.repository.UserRepository;
import com.All4Animal.server.repository.UserPreferenceRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final AnimalRepository animalRepository;
    private final UserPreferenceRepository userPreferenceRepository;
    private final UserRepository userRepository;

    private static final int BASE_SCORE = 50;

    @Transactional(readOnly = true)
    public AnimalFilterResponse recommendTop3AnimalsByPreference(Long userId){
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        UserPreference preference = userPreferenceRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("선호 데이터가 없습니다."));

        List<Animal> filteredAnimals = animalRepository.findAll(byPreference(preference));
        List<RecommendedAnimalResponse> top3Animals = filteredAnimals.stream()
                .sorted(Comparator
                        .comparingInt((Animal animal) -> calculateScore(user, preference, animal))
                        .reversed()
                        .thenComparing(Animal::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Animal::getAnimalId, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(3)
                .map(this::toRecommendedAnimalResponse)
                .toList();

        return new AnimalFilterResponse(top3Animals.size(), top3Animals);
    }

    private Specification<Animal> byPreference(UserPreference preference) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isFalse(root.get("isAdopted")));

            Animal.AnimalType animalType = mapAnimalType(preference.getPreferredAnimalType());
            if (animalType != null) {
                predicates.add(criteriaBuilder.equal(root.get("animalType"), animalType));
            }

            WeightRange weightRange = mapWeightRange(preference.getPreferredSize());
            if (weightRange != null) {
                predicates.add(criteriaBuilder.between(root.get("weight"), weightRange.min(), weightRange.max()));
            }

            Animal.Gender preferredGender = mapGender(preference.getPreferredGender());
            if (preferredGender != null) {
                predicates.add(criteriaBuilder.equal(root.get("animal_sex"), preferredGender));
            }

            AgeRange ageRange = mapAgeRange(preference.getPreferredAgeGroup());
            if (ageRange != null) {
                predicates.add(criteriaBuilder.isNotNull(root.get("animal_age")));
                predicates.add(criteriaBuilder.between(root.get("animal_age"), ageRange.birthYearMin(), ageRange.birthYearMax()));
            }

            query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Animal.AnimalType mapAnimalType(UserPreference.PreferredAnimalType preferredAnimalType) {
        if (preferredAnimalType == null || preferredAnimalType == UserPreference.PreferredAnimalType.ANY) {
            return null;
        }
        return Animal.AnimalType.valueOf(preferredAnimalType.name());
    }

    private Animal.Gender mapGender(UserPreference.PreferredGender preferredGender) {
        if (preferredGender == null || preferredGender == UserPreference.PreferredGender.ANY) {
            return null;
        }
        return Animal.Gender.valueOf(preferredGender.name());
    }

    private WeightRange mapWeightRange(UserPreference.PreferredSize preferredSize) {
        if (preferredSize == null || preferredSize == UserPreference.PreferredSize.ANY) {
            return null;
        }

        return switch (preferredSize) {
            case SMALL -> new WeightRange(0.0, 9.999);
            case MEDIUM -> new WeightRange(10.0, 24.999);
            case LARGE -> new WeightRange(25.0, Double.MAX_VALUE);
            case ANY -> null;
        };
    }

    private AgeRange mapAgeRange(UserPreference.PreferredAgeGroup preferredAgeGroup) {
        if (preferredAgeGroup == null || preferredAgeGroup == UserPreference.PreferredAgeGroup.ANY) {
            return null;
        }

        int currentYear = LocalDate.now().getYear();

        return switch (preferredAgeGroup) {
            case YOUNG -> new AgeRange(currentYear - 2, currentYear);
            case ADULT -> new AgeRange(currentYear - 6, currentYear - 2);
            case SENIOR -> new AgeRange(0, currentYear - 7);
            case ANY -> null;
        };
    }

    private int calculateScore(Users user, UserPreference preference, Animal animal) {
        int score = BASE_SCORE;

        boolean activeAnimal = isActiveAnimal(animal);
        boolean largeAnimal = classifySize(animal.getWeight()) == UserPreference.PreferredSize.LARGE;
        int animalAge = getAnimalAge(animal);

        score += calculateHousingScore(user, activeAnimal, largeAnimal);
        score += calculateEmptyTimeScore(user, animal, activeAnimal, animalAge);
        score += calculatePersonalityPreferenceScore(preference, animal);

        return score;
    }

    private boolean isActiveAnimal(Animal animal) {
        return zeroIfNull(animal.getActive_playful().getValue()) >= 0.7
                || zeroIfNull(animal.getOutdoor_activity().getValue()) >= 0.7;
    }


    private int calculateHousingScore(Users user, boolean activeAnimal, boolean largeAnimal) {
        if (user.getHousingType() == null) {
            return 0;
        }

        int score = 0;
        if (user.getHousingType() == Users.Housing.APARTMENT_VILLA && largeAnimal) {
            score -= 10;
        }
        if (activeAnimal && user.getHousingType() == Users.Housing.APARTMENT_VILLA) {
            score -= 10;
        }
        if (activeAnimal && (user.getHousingType() == Users.Housing.DETACHED_HOUSE
                || user.getHousingType() == Users.Housing.HOUSE_WITH_YARD)) {
            score += 10;
        }
        return score;
    }

    private int calculateEmptyTimeScore(Users user, Animal animal, boolean activeAnimal, int animalAge) {
        Integer emptyTime = user.getEmptyTime();
        if (emptyTime == null) {
            return 0;
        }

        int score = 0;
        boolean isDog = animal.getAnimalType() == Animal.AnimalType.DOG;
        boolean isCat = animal.getAnimalType() == Animal.AnimalType.CAT;
        boolean isYoungAnimal = animalAge >= 0 && animalAge < 2;

        if (isDog && emptyTime >= 8) {
            score -= 5;
        } else if (isDog && emptyTime >= 5) {
            score -= 3;
        }

        if (isDog && isYoungAnimal && emptyTime >= 5) {
            score -= 3;
        }

        if (isCat && isYoungAnimal && emptyTime >= 8) {
            score -= 3;
        }

        if (activeAnimal && emptyTime > 8) {
            score -= 3;
        }

        return score;
    }

    private int calculatePersonalityPreferenceScore(UserPreference preference, Animal animal) {
        if (!StringUtils.hasText(preference.getPreferredPersonality())) {
            return 0;
        }

        return List.of(preference.getPreferredPersonality().split(",")).stream()
                .map(String::trim)
                .filter(StringUtils::hasText)
                .mapToInt(code -> getAnimalPersonalityScore(animal, code))
                .sum();
    }

    private int scalePersonalityScore(Double score) {
        if (score == null) {
            return 0;
        }
        return (int) Math.round(score * 10);
    }


    private int getAnimalPersonalityScore(Animal animal, String code) {
        return switch (code) {
            case "people_friendly" -> scalePersonalityScore(animal.getPeople_friendly().getValue());
            case "active_playful" -> scalePersonalityScore(animal.getActive_playful().getValue());
            case "calm_quiet" -> scalePersonalityScore(animal.getCalm_quiet().getValue());
            case "adaptable" -> scalePersonalityScore(animal.getAdaptable().getValue());
            case "outdoor_activity" -> scalePersonalityScore(animal.getOutdoor_activity().getValue());
            case "animal_friendly" -> scalePersonalityScore(animal.getAnimal_friendly().getValue());
            case "beginner_possible" -> scalePersonalityScore(animal.getBeginner_possible().getValue());
            case "family_friendly" -> scalePersonalityScore(animal.getFamily_friendly().getValue());
            case "slow_bonding_ok" -> scalePersonalityScore(animal.getSlow_bonding_ok().getValue());
            default -> 0;
        };
    }


    private Double zeroIfNull(Double score) {
        return score == null ? 0.0 : score;
    }

    private int getAnimalAge(Animal animal) {
        Integer birthYear = animal.getAnimal_age();
        if (birthYear == null || birthYear <= 0) {
            return -1;
        }
        return LocalDate.now().getYear() - birthYear;
    }

    private UserPreference.PreferredSize classifySize(double weight) {
        if (weight < 10) {
            return UserPreference.PreferredSize.SMALL;
        }
        if (weight < 25) {
            return UserPreference.PreferredSize.MEDIUM;
        }
        return UserPreference.PreferredSize.LARGE;
    }

    private RecommendedAnimalResponse toRecommendedAnimalResponse(Animal animal) {
        List<String> imageUrls = animal.getImages().stream()
                .map(AnimalImage::getImageUrl)
                .toList();

        return new RecommendedAnimalResponse(
                animal.getAnimalId(),
                animal.getDesertionNo(),
                animal.getAnimalType(),
                animal.getSpecies(),
                animal.getWeight(),
                animal.getAnimal_age(),
                animal.getPersona(),
                animal.getAnimal_sex(),
                animal.isVaccinated(),
                animal.getDescription(),
                animal.isAdopted(),
                animal.getCreatedAt(),
                animal.getHappenPlace(),
                animal.getCareNm(),
                animal.getCareTel(),
                animal.getCareAddr(),
                imageUrls
        );
    }

    private record WeightRange(double min, double max) { }

    private record AgeRange(int birthYearMin, int birthYearMax) { }
}
