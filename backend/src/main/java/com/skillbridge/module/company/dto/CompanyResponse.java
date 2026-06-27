package com.skillbridge.module.company.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String name, industry, description, logoUrl, websiteUrl;
    private BigDecimal minCgpa, packageLpa;
    private Integer maxBacklogs, bondYears;
    private List<String> requiredSkills, aptitudeTopics;
    private String preparationRoadmap, interviewTips, placementHistory;
    private Boolean isActive;
    private List<CompanyRoundResponse> rounds;
    private LocalDateTime createdAt;
}
