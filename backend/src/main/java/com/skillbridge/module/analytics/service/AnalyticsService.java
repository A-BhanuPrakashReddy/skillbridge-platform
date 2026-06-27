package com.skillbridge.module.analytics.service;

import com.skillbridge.module.analytics.dto.*;

public interface AnalyticsService {
    DSAGrowthResponse getDSAGrowth(Long userId);
    AptitudeGrowthResponse getAptitudeGrowth(Long userId);
    ResumeScoreHistoryResponse getResumeScoreHistory(Long userId);
    ReadinessTrendResponse getReadinessTrend(Long userId);
}
