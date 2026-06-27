package com.skillbridge.module.dsa.dto;

import com.skillbridge.common.enums.DSAStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkProgressRequest {
    @NotNull private Long problemId;
    @NotNull private DSAStatus status;
    private String notes;
    private Integer timeTakenMins;
}
