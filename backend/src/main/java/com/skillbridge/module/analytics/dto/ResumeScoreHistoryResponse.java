package com.skillbridge.module.analytics.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ResumeScoreHistoryResponse {
    private List<String> dates;
    private List<Double> scores;
}
