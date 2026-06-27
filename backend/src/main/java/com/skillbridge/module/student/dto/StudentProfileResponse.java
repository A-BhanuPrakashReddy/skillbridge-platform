package com.skillbridge.module.student.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class StudentProfileResponse {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String college;
    private String branch;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private Integer activeBacklogs;
    private Integer totalBacklogs;
    private String phone;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private List<String> skills;
    private String photoUrl;
    private BigDecimal readinessScore;
    private LocalDateTime createdAt;
}
