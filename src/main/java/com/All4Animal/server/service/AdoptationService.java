package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import com.All4Animal.server.dto.response.AdoptationResponse;
import com.All4Animal.server.entity.Adoptation;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AdoptationRepository;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdoptationService {

    private final AdoptationRepository adoptationRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final S3Service s3Service;

    @Transactional
    public AdoptationResponse createInquiry(Long animalId) {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        if (animal.isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        Adoptation adoptation = adoptationRepository.findByUserAndAnimal(user, animal)
                .orElse(null);

        if (adoptation != null && adoptation.getStatus() != Adoptation.AdoptionStatus.INQUIRY) {
            return AdoptationResponse.from(adoptation, createProofImageUrl(adoptation));
        }

        if (adoptation == null) {
            adoptation = Adoptation.builder()
                    .user(user)
                    .animal(animal)
                    .build();
        }

        adoptation.setStatus(Adoptation.AdoptionStatus.INQUIRY);
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptationResponse.from(adoptationRepository.save(adoptation));
    }

    @Transactional
    public AdoptationResponse apply(Long animalId) {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        if (animal.isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        Adoptation adoptation = adoptationRepository.findByUserAndAnimal(user, animal)
                .orElseThrow(() -> new IllegalArgumentException("입양 문의 후 신청할 수 있습니다."));

        if (adoptation.getStatus() == Adoptation.AdoptionStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 입양 완료된 신청입니다.");
        }

        adoptation.setStatus(Adoptation.AdoptionStatus.APPLIED);
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptationResponse.from(adoptationRepository.save(adoptation), createProofImageUrl(adoptation));
    }

    @Transactional
    public AdoptationResponse uploadProofImage(Long adoptionId, MultipartFile image) {
        Long userId = authService.getCurrentUserId();

        Adoptation adoptation = adoptationRepository.findById(adoptionId)
                .orElseThrow(() -> new IllegalArgumentException("입양 신청을 찾을 수 없습니다."));

        if (!adoptation.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 입양 신청에만 사진을 등록할 수 있습니다.");
        }

        if (adoptation.getStatus() != Adoptation.AdoptionStatus.APPLIED) {
            throw new IllegalArgumentException("입양 신청 상태에서만 사진을 등록할 수 있습니다.");
        }

        if (adoptation.getAnimal().isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        S3PresignedUrlResponse uploadResponse = s3Service.uploadAdoptationProofImage(userId, image);
        adoptation.setProofImageKey(uploadResponse.getKey());
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptationResponse.from(adoptationRepository.save(adoptation), uploadResponse.getPreSignedUrl());
    }

    @Transactional
    public AdoptationResponse approve(Long adoptionId) {
        Users currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != Users.Role.MASTER) {
            throw new IllegalArgumentException("마스터 계정만 입양 승인할 수 있습니다.");
        }

        Adoptation adoptation = adoptationRepository.findById(adoptionId)
                .orElseThrow(() -> new IllegalArgumentException("입양 신청을 찾을 수 없습니다."));

        if (adoptation.getStatus() != Adoptation.AdoptionStatus.APPLIED) {
            throw new IllegalArgumentException("입양 신청 상태만 승인할 수 있습니다.");
        }

        if (adoptation.getProofImageKey() == null || adoptation.getProofImageKey().isBlank()) {
            throw new IllegalArgumentException("입양 완료 사진 등록 후 승인할 수 있습니다.");
        }

        Animal animal = adoptation.getAnimal();
        if (animal.isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        animal.setAdopted(true);
        adoptation.setStatus(Adoptation.AdoptionStatus.COMPLETED);
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptationResponse.from(adoptationRepository.save(adoptation), createProofImageUrl(adoptation));
    }

    public List<AdoptationResponse> getMyAdoptations() {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return adoptationRepository.findAllByUserWithAnimalOrderByUpdatedAtDesc(user).stream()
                .map(adoptation -> AdoptationResponse.from(adoptation, createProofImageUrl(adoptation)))
                .toList();
    }

    public List<AdoptationResponse> getAdoptations(Adoptation.AdoptionStatus status) {
        List<Adoptation> adoptations = status == null
                ? adoptationRepository.findAllWithAnimalOrderByUpdatedAtDesc()
                : adoptationRepository.findAllByStatusWithAnimalOrderByUpdatedAtDesc(status);

        return adoptations.stream()
                .map(adoptation -> AdoptationResponse.from(adoptation, createProofImageUrl(adoptation)))
                .toList();
    }

    private String createProofImageUrl(Adoptation adoptation) {
        String imageKey = adoptation.getProofImageKey();
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return s3Service.getGetS3Url(adoptation.getUser().getUserId(), imageKey).getPreSignedUrl();
    }
}
