package com.skillbridge.module.company.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "companies")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 200) private String name;
    @Column(length = 100) private String industry;
    @Column(columnDefinition = "TEXT") private String description;
    @Column(name = "min_cgpa", precision = 4, scale = 2) private BigDecimal minCgpa;
    @Column(name = "max_backlogs") private Integer maxBacklogs = 0;
    @Column(name = "required_skills", length = 1000) private String requiredSkills;
    @Column(name = "package_lpa", precision = 6, scale = 2) private BigDecimal packageLpa;
    @Column(name = "bond_years") private Integer bondYears = 0;
    @Column(name = "logo_url", length = 500) private String logoUrl;
    @Column(name = "website_url", length = 300) private String websiteUrl;
    @Column(name = "aptitude_topics", length = 500) private String aptitudeTopics;
    @Column(name = "preparation_roadmap", columnDefinition = "TEXT") private String preparationRoadmap;
    @Column(name = "interview_tips", columnDefinition = "TEXT") private String interviewTips;
    @Column(name = "placement_history", columnDefinition = "TEXT") private String placementHistory;
    @Column(name = "is_active", columnDefinition = "TINYINT(1)") private Boolean isActive = true;
    @Column(name = "is_deleted", columnDefinition = "TINYINT(1)") private Boolean isDeleted = false;
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true) @OrderBy("roundNumber ASC")
    private List<CompanyRound> rounds = new ArrayList<>();
    @CreatedDate @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;
    @LastModifiedDate @Column(name = "updated_at") private LocalDateTime updatedAt;

    public List<String> getRequiredSkillsList() {
        if (requiredSkills == null || requiredSkills.isBlank()) return new ArrayList<>();
        return Arrays.asList(requiredSkills.split(","));
    }
}
