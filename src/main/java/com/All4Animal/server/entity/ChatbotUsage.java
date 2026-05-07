package com.All4Animal.server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        name = "ChatbotUsage",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "usage_date"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatbotUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatbotUsageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;

    @Column(nullable = false)
    private Integer requestCount;

}
