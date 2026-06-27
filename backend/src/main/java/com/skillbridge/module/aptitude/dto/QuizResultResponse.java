package com.skillbridge.module.aptitude.dto;

import com.skillbridge.common.enums.AptitudeCategory;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizResultResponse {
    private Long attemptId;
    private AptitudeCategory category;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private BigDecimal scorePercentage;
    private Integer timeTakenSeconds;
    private List<QuestionResultDTO> questionResults;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class QuestionResultDTO {
        private Long questionId;
        private String questionText;
        private String optionA, optionB, optionC, optionD;
        private String selectedOption;
        private String correctOption;
        private Boolean isCorrect;
        private String explanation;
    }
}
