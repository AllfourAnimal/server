package com.All4Animal.server.service;

import com.All4Animal.server.entity.ChatbotUsage;
import com.All4Animal.server.entity.Users;
import com.All4Animal.server.repository.ChatbotUsageRepository;
import com.All4Animal.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChatbotUsageService {
    private static final int DAILY_LIMIT = 5;

    private final ChatbotUsageRepository chatbotUsageRepository;
    private final UserRepository userRepository;

    @Transactional
    public void checkAndIncrease(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();

        ChatbotUsage usage = chatbotUsageRepository.findByUserAndUsageDate(user, today)
                .orElseGet(() -> ChatbotUsage.builder()
                        .user(user)
                        .usageDate(today)
                        .requestCount(0)
                        .build());

        if (usage.getRequestCount() >= DAILY_LIMIT) {
            throw new RuntimeException("오늘 챗봇 질문 가능 횟수 " + DAILY_LIMIT + "회를 모두 사용했습니다.");
        }

        usage.setRequestCount(usage.getRequestCount() + 1);
        chatbotUsageRepository.save(usage);
    }
}
