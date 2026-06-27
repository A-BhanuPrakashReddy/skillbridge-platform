package com.skillbridge.module.analytics.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AptitudeGrowthResponse {
    private List<String> weeks;
    private List<Double> avgScores;
    private List<Long> attempts;
}
