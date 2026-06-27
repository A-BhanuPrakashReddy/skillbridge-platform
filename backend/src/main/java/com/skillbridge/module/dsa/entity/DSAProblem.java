package com.skillbridge.module.dsa.entity;

import com.skillbridge.common.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "dsa_problems")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DSAProblem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 100)
    private String topic;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(length = 50)
    private String platform;

    @Column(name = "problem_url", length = 500)
    private String problemUrl;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private boolean isDeleted = false;

    @CreatedDate @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
