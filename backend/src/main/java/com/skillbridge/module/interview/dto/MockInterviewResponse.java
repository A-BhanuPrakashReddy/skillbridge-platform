package com.skillbridge.module.interview.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MockInterviewResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long officerId;
    private String officerName;
    private LocalDateTime scheduledAt;
    private String status;
    private Double score;
    private String feedback;
    private String interviewType;
    private LocalDateTime createdAt;
}
