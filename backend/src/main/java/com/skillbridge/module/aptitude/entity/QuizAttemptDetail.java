package com.skillbridge.module.aptitude.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "quiz_attempt_details")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QuizAttemptDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "attempt_id", nullable = false) private QuizAttempt attempt;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "question_id", nullable = false) private AptitudeQuestion question;
    @Column(name = "selected_option", length = 1) private String selectedOption;
    @Column(name = "is_correct", nullable = false) private Boolean isCorrect = false;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
