package com.skillbridge.module.dsa.dto;

import com.skillbridge.common.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DSAProblemRequest {
    @NotBlank private String title;
    @NotBlank private String topic;
    @NotNull private Difficulty difficulty;
    private String platform;
    private String problemUrl;
}
