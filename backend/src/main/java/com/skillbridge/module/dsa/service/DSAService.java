package com.skillbridge.module.dsa.service;

import com.skillbridge.common.enums.Difficulty;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.dsa.dto.*;
import org.springframework.data.domain.Page;
import java.util.List;

public interface DSAService {
    ApiResponse<Page<DSAProblemResponse>> getProblems(String topic, Difficulty difficulty, int page, int size, Long userId);
    ApiResponse<DSAProblemResponse> getProblem(Long id, Long userId);
    ApiResponse<List<String>> getAllTopics();
    ApiResponse<DSAProgressResponse> markProgress(Long userId, MarkProgressRequest request);
    ApiResponse<Page<DSAProgressResponse>> getMyProgress(Long userId, int page, int size);
    ApiResponse<DSAStatsResponse> getStats(Long userId);
    ApiResponse<DSAStreakResponse> getStreak(Long userId);
    ApiResponse<List<String>> getWeakTopics(Long userId);
}
