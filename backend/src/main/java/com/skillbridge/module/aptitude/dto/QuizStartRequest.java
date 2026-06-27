package com.skillbridge.module.aptitude.dto;

import com.skillbridge.common.enums.AptitudeCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuizStartRequest {
    @NotNull private AptitudeCategory category;
    @Min(5) @Max(25) private Integer questionCount = 10;
}
