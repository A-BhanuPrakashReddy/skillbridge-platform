package com.skillbridge.module.company.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CompanyRoundResponse {
    private Long id;
    private Integer roundNumber;
    private String roundName, roundType, description, tips;
    private List<String> dsaTopics, aptitudeTopics;
    private Integer durationMinutes;
}
