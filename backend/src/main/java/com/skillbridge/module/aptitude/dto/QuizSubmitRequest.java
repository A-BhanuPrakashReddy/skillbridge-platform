package com.skillbridge.module.aptitude.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class QuizSubmitRequest {
    @NotNull private Long attemptId;
    @NotNull private List<QuizAnswerDTO> answers;
    private Integer timeTakenSeconds;

    @Data
    public static class QuizAnswerDTO {
        private Long questionId;
        private String selectedOption;
    }
}
