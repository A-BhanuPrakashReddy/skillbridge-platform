package com.skillbridge.module.dsa.dto;

import lombok.*;
import java.time.LocalDate;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DSAStreakResponse {
    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastSolvedDate;
    private Boolean solvedToday;
}
