package com.All4Animal.server.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity // Animal 클래스를 DB 테이블과 연결된 엔티티라고 선언
@Table(name = "Adoptation") // table 이름은 Animal로 설정
@NoArgsConstructor // 기본 생성자 자동 제작
@AllArgsConstructor // 전체 생성자 자동 제작
@Data // Getter, Setter, toString, equals과 같은 기본 메서드 자동 제작
@Builder // 빌더 패던 적용
public class Adoptation {
    public enum AdoptionStatus {
        INQUIRY,      // 입양 문의
        APPLIED,      // 입양 신청
        COMPLETED,    // 입양 완료
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adoptionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AdoptionStatus status;

    private String proofImageKey;

    private LocalDateTime updatedAt;
}
