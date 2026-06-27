package com.skillbridge.module.student.dto;

import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardSummaryResponse {
    private String name;
    private BigDecimal cgpa;
    private String branch;
    private BigDecimal readinessScore;
    private Long totalSolved;
    private Long totalProblems;
    private Integer currentStreak;
    private Integer longestStreak;
    private Double avgAptitudeScore;
    private Long totalQuizAttempts;
    private BigDecimal latestAtsScore;
    private Boolean hasResume;
    private Long eligibleCompaniesCount;
    private Long totalCompaniesCount;
    private Long pendingInterviews;
    private Long completedInterviews;
    private BigDecimal avgInterviewScore;
}
