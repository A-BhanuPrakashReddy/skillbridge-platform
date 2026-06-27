package com.skillbridge.module.aptitude.dto;

import com.skillbridge.common.enums.AptitudeCategory;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizStartResponse {
    private Long attemptId;
    private AptitudeCategory category;
    private Integer totalQuestions;
    private List<AptitudeQuestionResponse> questions;
}
