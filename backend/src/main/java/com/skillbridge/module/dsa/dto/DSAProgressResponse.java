package com.skillbridge.module.dsa.dto;

import com.skillbridge.common.enums.DSAStatus;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DSAProgressResponse {
    private Long id;
    private Long problemId;
    private String problemTitle;
    private String topic;
    private DSAStatus status;
    private String notes;
    private Integer timeTakenMins;
    private LocalDate solvedAt;
    private LocalDateTime updatedAt;
}
