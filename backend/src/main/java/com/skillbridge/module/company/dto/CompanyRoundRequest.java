package com.skillbridge.module.company.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CompanyRoundRequest {
    @NotNull @Min(1) private Integer roundNumber;
    @NotBlank private String roundName;
    @NotBlank private String roundType;
    private String description;
    private List<String> dsaTopics;
    private List<String> aptitudeTopics;
    private String tips;
    @Min(0) private Integer durationMinutes;
}
