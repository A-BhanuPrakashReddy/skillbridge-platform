package com.skillbridge.module.company.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "company_rounds")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CompanyRound {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "company_id", nullable = false) private Company company;
    @Column(name = "round_number", nullable = false) private Integer roundNumber;
    @Column(name = "round_name", nullable = false, length = 100) private String roundName;
    @Column(name = "round_type", nullable = false, length = 50) private String roundType;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "dsa_topics", length = 500) private String dsaTopics;
    @Column(name = "aptitude_topics", length = 300) private String aptitudeTopics;
    @Column(columnDefinition = "TEXT") private String tips;
    @Column(name = "duration_minutes") private Integer durationMinutes;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)") private Boolean isDeleted = false;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
