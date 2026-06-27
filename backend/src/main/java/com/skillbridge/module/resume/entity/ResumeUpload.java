package com.skillbridge.module.resume.entity;

import com.skillbridge.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "resume_uploads")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ResumeUpload {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false) private User user;
    @Column(name = "file_name", length = 255) private String fileName;
    @Column(name = "cloudinary_url", nullable = false, length = 500) private String cloudinaryUrl;
    @Column(name = "cloudinary_public_id", nullable = false, length = 300) private String cloudinaryPublicId;
    @Column(name = "ats_score", precision = 5, scale = 2) private BigDecimal atsScore;
    @Column(name = "ats_feedback", columnDefinition = "JSON") private String atsFeedback;
    @Column(name = "version_number") private Integer versionNumber = 1;
    @Column(name = "is_latest", columnDefinition = "TINYINT(1)") private Boolean isLatest = true;
    @Column(name = "uploaded_at") private LocalDateTime uploadedAt;
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;
}
