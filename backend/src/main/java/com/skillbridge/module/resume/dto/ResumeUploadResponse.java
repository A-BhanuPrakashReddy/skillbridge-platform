package com.skillbridge.module.resume.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResumeUploadResponse {
    private Long id;
    private String fileName;
    private String cloudinaryUrl;
    private BigDecimal atsScore;
    private ATSFeedbackDTO feedback;
    private Integer versionNumber;
    private LocalDateTime uploadedAt;
}
