package com.skillbridge.module.aptitude.entity;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "quiz_attempts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QuizAttempt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50) private AptitudeCategory category;
    @Column(name = "total_questions", nullable = false) private Integer totalQuestions;
    @Column(name = "correct_answers") private Integer correctAnswers = 0;
    @Column(name = "wrong_answers") private Integer wrongAnswers = 0;
    @Column(name = "score_percentage", precision = 5, scale = 2) private BigDecimal scorePercentage;
    @Column(name = "time_taken_seconds") private Integer timeTakenSeconds;
    @Column(name = "attempted_at") private LocalDateTime attemptedAt;
    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL) private List<QuizAttemptDetail> details = new ArrayList<>();
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
}
