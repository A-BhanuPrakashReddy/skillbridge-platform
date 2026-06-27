package com.skillbridge.module.aptitude.entity;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity @Table(name = "aptitude_questions")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AptitudeQuestion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;
    @Column(name = "option_a", nullable = false, length = 300) private String optionA;
    @Column(name = "option_b", nullable = false, length = 300) private String optionB;
    @Column(name = "option_c", nullable = false, length = 300) private String optionC;
    @Column(name = "option_d", nullable = false, length = 300) private String optionD;
    @Column(name = "correct_option", nullable = false, length = 1) private String correctOption;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 50) private AptitudeCategory category;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private Difficulty difficulty;
    @Column(columnDefinition = "TEXT") private String explanation;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)") private boolean isDeleted = false;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
