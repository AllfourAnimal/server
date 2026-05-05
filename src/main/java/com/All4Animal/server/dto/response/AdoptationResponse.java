package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Adoptation;
import com.All4Animal.server.entity.Animal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdoptationResponse {

    private Long adoptionId;
    private Long userId;
    private Long animalId;
    private String animalSpecies;
    private Animal.AnimalType animalType;
    private String careNm;
    private String careTel;
    private String careAddr;
    private Adoptation.AdoptionStatus status;
    private String proofImageKey;
    private String proofImageUrl;
    private LocalDateTime updatedAt;

    public static AdoptationResponse from(Adoptation adoptation) {
        return from(adoptation, null);
    }

    public static AdoptationResponse from(Adoptation adoptation, String proofImageUrl) {
        return new AdoptationResponse(
                adoptation.getAdoptionId(),
                adoptation.getUser().getUserId(),
                adoptation.getAnimal().getAnimalId(),
                adoptation.getAnimal().getSpecies(),
                adoptation.getAnimal().getAnimalType(),
                adoptation.getAnimal().getCareNm(),
                adoptation.getAnimal().getCareTel(),
                adoptation.getAnimal().getCareAddr(),
                adoptation.getStatus(),
                adoptation.getProofImageKey(),
                proofImageUrl,
                adoptation.getUpdatedAt()
        );
    }
}
