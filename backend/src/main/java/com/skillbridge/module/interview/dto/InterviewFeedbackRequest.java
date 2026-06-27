package com.skillbridge.module.interview.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewFeedbackRequest {
    @NotNull
    @Min(0) @Max(100)
    private Double score;

    private String feedback;
}
