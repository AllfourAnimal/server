package com.All4Animal.server.controller;

import com.All4Animal.server.dto.request.ChatbotReqeust;
import com.All4Animal.server.service.AuthService;
import com.All4Animal.server.service.ChatbotService;
import com.All4Animal.server.service.ChatbotUsageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@Tag(name = "ChatBot", description = "사용자의 질문에 답변하는 챗봇")
public class ChatBotController {
    private final ChatbotService chatBotService;
    private final AuthService authService;
    private final ChatbotUsageService chatbotUsageService;

    @PostMapping("/ask")
    @Operation(summary = "챗봇 질문하기", description = "키워드를 입력하면 펫봇이 답변을 생성합니다.")
    public String responseAsk(@RequestBody ChatbotReqeust keyword) {
        Long userId = authService.getCurrentUserId();

        chatbotUsageService.checkAndIncrease(userId);

        return chatBotService.generateChatReply(keyword.getKeyword());
    }
}
