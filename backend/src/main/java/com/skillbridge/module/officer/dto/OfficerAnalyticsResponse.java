package com.skillbridge.module.officer.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class OfficerAnalyticsResponse {
    private Long totalStudents;
    private Double avgReadinessScore;
    private Long studentsAbove70;
    private Long studentsBelow50;
    private Map<String, Long> branchWiseCount;
    private Map<String, Double> branchWiseAvgReadiness;
}
