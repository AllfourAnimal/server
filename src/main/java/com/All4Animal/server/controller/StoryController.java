package com.All4Animal.server.controller;

import com.All4Animal.server.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/stories")
@RequiredArgsConstructor
@Tag(name = "Story", description = "유기동물 스토리 API")
public class StoryController {

    private final StoryService storyService;

    @PostMapping("/{animalId}")
    @Operation(summary = "스토리 생성 및 저장", description = "동물 정보를 기반으로 AI가 스토리 생성 및 DB 저장")
    public ResponseEntity<String> createStory(@PathVariable Long animalId) {
        String story = storyService.createAndSaveStory(animalId);
        return ResponseEntity.ok(story);
    }

}
