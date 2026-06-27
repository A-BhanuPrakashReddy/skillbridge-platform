package com.skillbridge.module.resume.dto;

import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ATSFeedbackDTO {
    private List<String> presentSkills;
    private List<String> missingSkills;
    private List<String> missingSections;
    private List<String> suggestions;
    private List<String> strengths;
    private Integer sectionsScore;
    private Integer skillsScore;
    private Integer formattingScore;
    private Integer actionVerbsScore;
    private Integer totalScore;
}
