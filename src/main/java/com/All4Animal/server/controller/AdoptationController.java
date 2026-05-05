package com.All4Animal.server.controller;

import com.All4Animal.server.dto.response.AdoptationResponse;
import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.service.AdoptationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.All4Animal.server.entity.Adoptation;

import java.util.List;

@RestController
@RequestMapping("/api/adoptations")
@RequiredArgsConstructor
@Tag(name = "Adoptation", description = "입양 문의 API")
public class AdoptationController {

    private final AdoptationService adoptationService;

    @Operation(summary = "입양 문의", description = "로그인한 사용자와 동물을 매핑해 입양 문의 상태로 저장합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "입양 문의 저장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "adoptionId": 1,
                                              "userId": 1,
                                              "animalId": 10,
                                              "animalSpecies": "푸들",
                                              "animalType": "DOG",
                                              "status": "INQUIRY",
                                              "proofImageKey": null,
                                              "proofImageUrl": null,
                                              "updatedAt": "2026-05-05T12:00:00"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자 또는 동물 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/{animalId}/inquiry")
    public ResponseEntity<AdoptationResponse> createInquiry(@PathVariable Long animalId) {
        return ResponseEntity.ok(adoptationService.createInquiry(animalId));
    }

    @Operation(summary = "입양 신청", description = "입양 문의 상태인 건을 입양 신청 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "입양 신청 상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "adoptionId": 1,
                                              "userId": 1,
                                              "animalId": 10,
                                              "animalSpecies": "푸들",
                                              "animalType": "DOG",
                                              "status": "APPLIED",
                                              "proofImageKey": null,
                                              "proofImageUrl": null,
                                              "updatedAt": "2026-05-05T12:10:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{animalId}/apply")
    public ResponseEntity<AdoptationResponse> apply(@PathVariable Long animalId) {
        return ResponseEntity.ok(adoptationService.apply(animalId));
    }

    @Operation(summary = "입양 완료 사진 등록", description = "입양 신청 상태에서 입양 완료 증빙 사진을 S3에 업로드하고 신청 건에 저장합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "입양 완료 사진 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "adoptionId": 1,
                                              "userId": 1,
                                              "animalId": 10,
                                              "animalSpecies": "푸들",
                                              "animalType": "DOG",
                                              "status": "APPLIED",
                                              "proofImageKey": "adoptation/1/550e8400-e29b-41d4-a716-446655440000/dog.png",
                                              "proofImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/...",
                                              "updatedAt": "2026-05-05T12:20:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping(value = "/{adoptionId}/proof-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdoptationResponse> uploadProofImage(
            @PathVariable Long adoptionId,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(adoptationService.uploadProofImage(adoptionId, image));
    }

    @Operation(summary = "입양 승인", description = "입양 완료 사진이 등록된 입양 신청 건을 승인해 입양 완료 상태로 변경합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "입양 승인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "adoptionId": 1,
                                              "userId": 1,
                                              "animalId": 10,
                                              "animalSpecies": "푸들",
                                              "animalType": "DOG",
                                              "status": "COMPLETED",
                                              "proofImageKey": "adoptation/1/550e8400-e29b-41d4-a716-446655440000/dog.png",
                                              "proofImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/...",
                                              "updatedAt": "2026-05-05T12:30:00"
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{adoptionId}/approve")
    public ResponseEntity<AdoptationResponse> approve(@PathVariable Long adoptionId) {
        return ResponseEntity.ok(adoptationService.approve(adoptionId));
    }

    @Operation(summary = "내 입양 문의/신청 목록 조회", description = "현재 로그인한 사용자의 입양 문의, 입양 신청, 입양 완료 내역을 최신순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "내 입양 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            [
                                              {
                                                "adoptionId": 3,
                                                "userId": 1,
                                                "animalId": 12,
                                                "animalSpecies": "코리안 숏헤어",
                                                "animalType": "CAT",
                                                "status": "COMPLETED",
                                                "proofImageKey": "adoptation/1/550e8400-e29b-41d4-a716-446655440000/cat.png",
                                                "proofImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/...",
                                                "updatedAt": "2026-05-05T12:30:00"
                                              },
                                              {
                                                "adoptionId": 1,
                                                "userId": 1,
                                                "animalId": 10,
                                                "animalSpecies": "푸들",
                                                "animalType": "DOG",
                                                "status": "INQUIRY",
                                                "proofImageKey": null,
                                                "proofImageUrl": null,
                                                "updatedAt": "2026-05-05T12:00:00"
                                              }
                                            ]
                                            """
                            )
                    )
            )
    })
    @GetMapping("/my")
    public ResponseEntity<List<AdoptationResponse>> getMyAdoptations() {
        return ResponseEntity.ok(adoptationService.getMyAdoptations());
    }

    @Operation(summary = "입양 문의/신청 전체 조회", description = "입양 문의, 입양 신청, 입양 완료 내역을 최신순으로 조회합니다. status 쿼리로 INQUIRY, APPLIED, COMPLETED 중 하나를 필터링할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "입양 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdoptationResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "전체 조회",
                                            value = """
                                                    [
                                                      {
                                                        "adoptionId": 2,
                                                        "userId": 4,
                                                        "animalId": 21,
                                                        "animalSpecies": "믹스견",
                                                        "animalType": "DOG",
                                                        "status": "APPLIED",
                                                        "proofImageKey": "adoptation/4/550e8400-e29b-41d4-a716-446655440000/dog.png",
                                                        "proofImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/...",
                                                        "updatedAt": "2026-05-05T13:00:00"
                                                      }
                                                    ]
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "입양 신청만 조회",
                                            value = """
                                                    [
                                                      {
                                                        "adoptionId": 2,
                                                        "userId": 4,
                                                        "animalId": 21,
                                                        "animalSpecies": "믹스견",
                                                        "animalType": "DOG",
                                                        "status": "APPLIED",
                                                        "proofImageKey": "adoptation/4/550e8400-e29b-41d4-a716-446655440000/dog.png",
                                                        "proofImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/...",
                                                        "updatedAt": "2026-05-05T13:00:00"
                                                      }
                                                    ]
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<AdoptationResponse>> getAdoptations(
            @RequestParam(required = false) Adoptation.AdoptionStatus status
    ) {
        return ResponseEntity.ok(adoptationService.getAdoptations(status));
    }
}
