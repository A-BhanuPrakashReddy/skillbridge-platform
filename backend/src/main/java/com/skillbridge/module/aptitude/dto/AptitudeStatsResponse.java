package com.skillbridge.module.aptitude.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AptitudeStatsResponse {
    private Double quantitativeAvg;
    private Double logicalAvg;
    private Double verbalAvg;
    private Double overallAvg;
    private Long totalAttempts;
    private List<String> weakAreas;
    private List<String> strongAreas;
}
