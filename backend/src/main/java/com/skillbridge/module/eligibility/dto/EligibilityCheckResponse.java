package com.skillbridge.module.eligibility.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EligibilityCheckResponse {
    private Long companyId;
    private String companyName;
    private Boolean isEligible;
    private List<EligibilityCriterion> criteria;
    private Integer matchScore;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EligibilityCriterion {
        private String criterionName;
        private Boolean isPassed;
        private String required;
        private String actual;
        private String message;
    }
}
