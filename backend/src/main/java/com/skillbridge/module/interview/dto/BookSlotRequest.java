package com.skillbridge.module.interview.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookSlotRequest {
    @NotNull
    @Future
    private LocalDateTime scheduledAt;

    private String interviewType;
}
