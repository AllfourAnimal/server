package com.All4Animal.server.service;

import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Favorite;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.FavoriteRepository;
import com.All4Animal.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository usersRepository; // 사용자 레포지토리 명칭 확인 필요

    @Transactional
    public String toggleFavorite(Long userId, Long animalId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
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
}
