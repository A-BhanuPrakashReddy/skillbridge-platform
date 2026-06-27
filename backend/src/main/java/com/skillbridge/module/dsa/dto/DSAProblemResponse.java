package com.skillbridge.module.dsa.dto;

import com.skillbridge.common.enums.DSAStatus;
import com.skillbridge.common.enums.Difficulty;
import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DSAProblemResponse {
    private Long id;
    private String title;
    private String topic;
    private Difficulty difficulty;
    private String platform;
    private String problemUrl;
    private DSAStatus userStatus;
    private LocalDateTime createdAt;
}
