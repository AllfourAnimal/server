package com.All4Animal.server.repository;

import com.All4Animal.server.entity.ChatbotUsage;
import com.All4Animal.server.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ChatbotUsageRepository extends JpaRepository<ChatbotUsage, Long> {
    Optional<ChatbotUsage> findByUserAndUsageDate(Users user, LocalDate usageDate);
}
