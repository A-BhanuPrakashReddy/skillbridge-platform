package com.skillbridge.module.aptitude.dto;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AptitudeQuestionRequest {
    @NotBlank private String questionText;
    @NotBlank private String optionA;
    @NotBlank private String optionB;
    @NotBlank private String optionC;
    @NotBlank private String optionD;
    @NotBlank private String correctOption;
    @NotNull private AptitudeCategory category;
    @NotNull private Difficulty difficulty;
    private String explanation;
}
