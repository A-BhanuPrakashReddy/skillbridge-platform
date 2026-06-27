package com.skillbridge.module.student.entity;

import com.skillbridge.module.auth.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "student_profiles")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class StudentProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String college;
    private String branch;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Column(name = "active_backlogs")
    private Integer activeBacklogs = 0;

    @Column(name = "total_backlogs")
    private Integer totalBacklogs = 0;

    private String phone;

    @Column(name = "linkedin_url", length = 300)
    private String linkedinUrl;

    @Column(name = "github_url", length = 300)
    private String githubUrl;

    @Column(name = "portfolio_url", length = 300)
    private String portfolioUrl;

    @Column(name = "skills", length = 1000)
    private String skills;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "readiness_score", precision = 5, scale = 2)
    private BigDecimal readinessScore = BigDecimal.ZERO;

    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)")
    private boolean isDeleted = false;

    @CreatedDate @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public List<String> getSkillsList() {
        if (skills == null || skills.isBlank()) return new ArrayList<>();
        return Arrays.asList(skills.split(","));
    }

    public void setSkillsList(List<String> skillList) {
        this.skills = (skillList != null && !skillList.isEmpty()) ? String.join(",", skillList) : "";
    }
}
