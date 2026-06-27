package com.skillbridge.module.aptitude.dto;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.enums.Difficulty;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AptitudeQuestionResponse {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private AptitudeCategory category;
    private Difficulty difficulty;
    // correctOption intentionally omitted for quiz display
}
