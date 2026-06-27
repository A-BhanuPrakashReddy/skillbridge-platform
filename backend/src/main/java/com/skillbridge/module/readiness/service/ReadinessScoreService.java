package com.skillbridge.module.readiness.service;

import java.math.BigDecimal;

public interface ReadinessScoreService {
    BigDecimal computeAndSave(Long userId);
    String getCategory(BigDecimal score);
}
