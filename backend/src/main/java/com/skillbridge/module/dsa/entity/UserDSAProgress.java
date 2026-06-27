package com.skillbridge.module.dsa.entity;

import com.skillbridge.common.enums.DSAStatus;
import com.skillbridge.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_dsa_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "problem_id"}))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UserDSAProgress {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private DSAProblem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DSAStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "time_taken_mins")
    private Integer timeTakenMins;

    @Column(name = "solved_at")
    private LocalDate solvedAt;

    @CreatedDate @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
