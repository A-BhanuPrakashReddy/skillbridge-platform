package com.skillbridge.module.company.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CompanyRequest {
    @NotBlank private String name;
    private String industry, description, logoUrl, websiteUrl;
    @DecimalMin("0.0") @DecimalMax("10.0") private BigDecimal minCgpa;
    @Min(0) private Integer maxBacklogs;
    private List<String> requiredSkills;
    @DecimalMin("0.0") private BigDecimal packageLpa;
    @Min(0) private Integer bondYears;
    private List<String> aptitudeTopics;
    private String preparationRoadmap, interviewTips, placementHistory;
}
