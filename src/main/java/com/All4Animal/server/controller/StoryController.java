package com.All4Animal.server.controller;

import com.All4Animal.server.dto.response.ErrorResponse;
import com.All4Animal.server.entity.Story;
import com.All4Animal.server.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stories") // 경로 시작 부분에 '/' 추가 권장
@RequiredArgsConstructor
@Tag(name = "Story", description = "유기동물 스토리 API (AI 기반)")
public class StoryController {

    private final StoryService storyService;

    @Operation(summary = "특정 동물 스토리 생성", description = "동물 정보를 기반으로 AI가 스토리를 생성하고 DB에 저장합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스토리 생성 및 저장 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    name = "success",
                                    value = "추운 겨울날 골목길에서 발견된 아이입니다. 지금은 활발하게 뛰어놀며 가족을 기다리고 있어요."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "동물 정보를 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "not_found",
                                    value = """
                                  {
                                    "code": "ANIMAL_NOT_FOUND",
                                    "message": "해당 ID의 동물 데이터가 존재하지 않아 스토리를 생성할 수 없습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    @PostMapping("/create/{animalId}")
    public ResponseEntity<String> createStory(@PathVariable Long animalId) {
        String story = storyService.createAndSaveStory(animalId);
        return ResponseEntity.ok(story);
    }

    @Operation(summary = "모든 동물 스토리 일괄 생성", description = "DB에 등록된 모든 유기동물에 대해 AI 스토리를 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "일괄 생성 프로세스 완료",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "모든 동물의 스토리 생성 시작 및 완료"
                            )
                    )
            )
    })
    @PostMapping("/create/all")
    public ResponseEntity<String> createAllStories() {
        storyService.createStoryAll();
        return ResponseEntity.ok("모든 동물의 스토리 생성 시작 및 완료");
    }

    @Operation(summary = "특정 동물 스토리 조회", description = "동물 ID를 사용하여 저장된 AI 스토리를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "스토리 조회 성공",
                    content = @Content(
                            mediaType = "text/plain",
                            examples = @ExampleObject(
                                    value = "사람을 무척 좋아하고 애교가 많은 개냥이입니다. 간식 소리만 들리면 먼저 달려와요."
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "스토리가 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "story_not_found",
                                    value = """
                                  {
                                    "code": "STORY_NOT_FOUND",
                                    "message": "해당 동물에 대해 생성된 스토리가 없습니다."
                                  }
                                  """
                            )
                    )
            )
    })
    @GetMapping("/{animalId}")
    public ResponseEntity<String> getStory(@PathVariable Long animalId) {
        Story story = storyService.getAnimalStory(animalId);
        return ResponseEntity.ok(story.getStoryContent());
    }
}