package com.All4Animal.server.service;

import com.All4Animal.server.dto.response.FavoriteResponse;
import com.All4Animal.server.entity.*;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.FavoriteRepository;
import com.All4Animal.server.repository.StoryRepository;
import com.All4Animal.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository usersRepository; // 사용자 레포지토리 명칭 확인 필요
    private final StoryRepository storyRepository;

    @Transactional
    public String toggleFavorite(Long userId, Long animalId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        String loginIdFromToken = authentication.getName();

        if (!user.getLoginId().equals(loginIdFromToken)) {
            throw new RuntimeException("본인의 계정으로만 찜하기가 가능합니다.");
        }

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        return favoriteRepository.findByUserAndAnimal(user, animal)
                .map(favorite -> {
                    favoriteRepository.delete(favorite);
                    return "찜 취소 완료";
                })
                .orElseGet(() -> {
                    Favorite favorite = Favorite.builder()
                            .user(user)
                            .animal(animal)
                            .build();
                    favoriteRepository.save(favorite);
                    return "찜 등록 완료";
                });
        }

    @Transactional
    public List<FavoriteResponse> getMyFavoriteAnimals() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        String loginId = authentication.getName();

        Users user = usersRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return favoriteRepository.findAllByUser(user).stream()
                .map(favorite -> {
                    Animal animal = favorite.getAnimal();

                    String storyContent = storyRepository.findByAnimal(animal)
                            .map(Story::getStoryContent)
                            .orElse("아직 생성된 스토리가 없습니다.");

                    return new FavoriteResponse(
                            animal.getSpecies(),
                            animal.getImages(), // AnimalImage 리스트
                            animal.getAnimal_age().longValue(),
                            FavoriteResponse.Gender.valueOf(animal.getAnimal_sex().name()),
                            storyContent
                    );
                })
                .collect(Collectors.toList());
    }

//    @Transactional
//    public List<Animal> getMyFavoriteAnimals() {
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new RuntimeException("로그인이 필요한 서비스입니다.");
//        }
//
//        String loginId = authentication.getName();
//
//        Users user = usersRepository.findByLoginId(loginId)
//                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
//
//        return favoriteRepository.findAllByUser(user).stream()
//                .map(Favorite::getAnimal)
//                .collect(Collectors.toList());
//    }
}
