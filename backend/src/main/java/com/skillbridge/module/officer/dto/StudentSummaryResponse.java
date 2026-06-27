package com.skillbridge.module.officer.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class StudentSummaryResponse {
    private Long id;
    private String name;
    private String email;
    private String branch;
    private BigDecimal cgpa;
    private Integer activeBacklogs;
    private BigDecimal readinessScore;
    private String skills;
    private String photoUrl;
}
