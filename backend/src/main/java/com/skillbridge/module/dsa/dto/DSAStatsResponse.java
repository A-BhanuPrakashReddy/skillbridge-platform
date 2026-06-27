package com.skillbridge.module.dsa.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DSAStatsResponse {
    private Long totalSolved;
    private Long totalAttempted;
    private Long totalRevisit;
    private Long easyCount;
    private Long mediumCount;
    private Long hardCount;
    private Map<String, Long> solvedByTopic;
    private Map<String, Long> totalByTopic;
    private List<String> weakTopics;
}
