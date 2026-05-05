package com.All4Animal.server.dto.response;

import com.All4Animal.server.entity.Adoption;
import com.All4Animal.server.entity.Animal;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AdoptionResponse {

    private Long adoptionId;
    private Long userId;
    private Long animalId;
    private String desertionNo;
    private String animalSpecies;
    private Animal.AnimalType animalType;
    private String careNm;
    private String careTel;
    private String careAddr;
    private Adoption.AdoptionStatus status;
    private String proofImageKey;
    private String proofImageUrl;
    private boolean reviewWritten;
    private LocalDateTime updatedAt;

    public static AdoptionResponse from(Adoption adoptation) {
        return from(adoptation, null);
    }

    public static AdoptionResponse from(Adoption adoptation, String proofImageUrl) {
        return from(adoptation, proofImageUrl, false);
    }

    public static AdoptionResponse from(Adoption adoptation, String proofImageUrl, boolean reviewWritten) {
        return new AdoptionResponse(
                adoptation.getAdoptionId(),
                adoptation.getUser().getUserId(),
                adoptation.getAnimal().getAnimalId(),
                adoptation.getAnimal().getDesertionNo(),
                adoptation.getAnimal().getSpecies(),
                adoptation.getAnimal().getAnimalType(),
                adoptation.getAnimal().getCareNm(),
                adoptation.getAnimal().getCareTel(),
                adoptation.getAnimal().getCareAddr(),
                adoptation.getStatus(),
                adoptation.getProofImageKey(),
                proofImageUrl,
                reviewWritten,
                adoptation.getUpdatedAt()
        );
    }
}
