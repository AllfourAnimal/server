package com.All4Animal.server.service;

import com.All4Animal.server.dto.request.S3PresignedUrlResponse;
import com.All4Animal.server.dto.response.AdoptionResponse;
import com.All4Animal.server.entity.Adoption;
import com.All4Animal.server.entity.Animal;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.AdoptionRepository;
import com.All4Animal.server.repository.AnimalRepository;
import com.All4Animal.server.repository.ReviewRepository;
import com.All4Animal.server.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRepository adoptationRepository;
    private final AnimalRepository animalRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final S3Service s3Service;

    @Transactional
    public AdoptionResponse createInquiry(Long animalId) {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        if (animal.isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        Adoption adoptation = adoptationRepository.findByUserAndAnimal(user, animal)
                .orElse(null);

        if (adoptation != null && adoptation.getStatus() != Adoption.AdoptionStatus.INQUIRY) {
            return AdoptionResponse.from(adoptation, createProofImageUrl(adoptation));
        }

        if (adoptation == null) {
            adoptation = Adoption.builder()
                    .user(user)
                    .animal(animal)
                    .build();
        }

        adoptation.setStatus(Adoption.AdoptionStatus.INQUIRY);
        adoptation.setUpdatedAt(LocalDateTime.now());
        return AdoptionResponse.from(adoptationRepository.save(adoptation));
    }

    @Transactional
    public AdoptionResponse apply(Long animalId) {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("동물을 찾을 수 없습니다."));

        if (animal.isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        Adoption adoptation = adoptationRepository.findByUserAndAnimal(user, animal)
                .orElseThrow(() -> new IllegalArgumentException("입양 문의 후 신청할 수 있습니다."));

        if (adoptation.getStatus() == Adoption.AdoptionStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 입양 완료된 신청입니다.");
        }

        adoptation.setStatus(Adoption.AdoptionStatus.APPLIED);
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptionResponse.from(adoptationRepository.save(adoptation), createProofImageUrl(adoptation));
    }

    @Transactional
    public AdoptionResponse uploadProofImage(Long adoptionId, MultipartFile image) {
        Long userId = authService.getCurrentUserId();

        Adoption adoptation = adoptationRepository.findById(adoptionId)
                .orElseThrow(() -> new IllegalArgumentException("입양 신청을 찾을 수 없습니다."));

        if (!adoptation.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인의 입양 신청에만 사진을 등록할 수 있습니다.");
        }

        if (adoptation.getStatus() != Adoption.AdoptionStatus.APPLIED) {
            throw new IllegalArgumentException("입양 신청 상태에서만 사진을 등록할 수 있습니다.");
        }

        if (adoptation.getAnimal().isAdopted()) {
            throw new IllegalArgumentException("이미 입양 완료된 동물입니다.");
        }

        S3PresignedUrlResponse uploadResponse = s3Service.uploadAdoptationProofImage(userId, image);
        adoptation.setProofImageKey(uploadResponse.getKey());
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptionResponse.from(adoptationRepository.save(adoptation), uploadResponse.getPreSignedUrl());
    }

    @Transactional
    public AdoptionResponse approve(Long adoptionId) {
        Users currentUser = authService.getCurrentUser();
        if (currentUser.getRole() != Users.Role.MASTER) {
            throw new IllegalArgumentException("마스터 계정만 입양 승인할 수 있습니다.");
        }

        Adoption adoptation = adoptationRepository.findById(adoptionId)
                .orElseThrow(() -> new IllegalArgumentException("입양 신청을 찾을 수 없습니다."));

        if (adoptation.getStatus() != Adoption.AdoptionStatus.APPLIED) {
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
        adoptation.setStatus(Adoption.AdoptionStatus.COMPLETED);
        adoptation.setUpdatedAt(LocalDateTime.now());

        return AdoptionResponse.from(adoptationRepository.save(adoptation), createProofImageUrl(adoptation));
    }

    public List<AdoptionResponse> getMyAdoptations() {
        Long userId = authService.getCurrentUserId();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Set<Long> reviewedAnimalIds = new HashSet<>(reviewRepository.findReviewedAnimalIdsByUser(user));

        return adoptationRepository.findAllByUserIdWithAnimalOrderByUpdatedAtDesc(userId).stream()
                .map(adoptation -> AdoptionResponse.from(
                        adoptation,
                        createProofImageUrl(adoptation),
                        reviewedAnimalIds.contains(adoptation.getAnimal().getAnimalId())
                ))
                .toList();
    }

    public List<AdoptionResponse> getAdoptations(Adoption.AdoptionStatus status) {
        List<Adoption> adoptations = status == null
                ? adoptationRepository.findAllWithAnimalOrderByUpdatedAtDesc()
                : adoptationRepository.findAllByStatusWithAnimalOrderByUpdatedAtDesc(status);

        return adoptations.stream()
                .map(adoptation -> AdoptionResponse.from(adoptation, createProofImageUrl(adoptation)))
                .toList();
    }

    private String createProofImageUrl(Adoption adoptation) {
        String imageKey = adoptation.getProofImageKey();
        if (imageKey == null || imageKey.isBlank()) {
            return null;
        }
        return s3Service.getGetS3Url(adoptation.getUser().getUserId(), imageKey).getPreSignedUrl();
    }
}
