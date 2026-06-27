package com.skillbridge.module.analytics.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DSAGrowthResponse {
    private List<String> weeks;
    private List<Long> solved;
}
