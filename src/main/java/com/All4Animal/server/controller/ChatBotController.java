package com.All4Animal.server.controller;

import com.All4Animal.server.service.ChatBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/chatbot")
@RequiredArgsConstructor
@Tag(name = "ChatBot", description = "사용자의 질문에 답변하는 챗봇")
public class ChatBotController {
    private final ChatBotService chatBotService;

    @PostMapping("/ask")
    @Operation(summary = "챗봇 질문하기", description = "키워드를 입력하면 펫봇이 답변을 생성합니다.")
    public String responseAsk(@RequestParam(name = "keyword") String keyword) {
        String response = chatBotService.generateChatReply(keyword);

        return response;
    }
}
