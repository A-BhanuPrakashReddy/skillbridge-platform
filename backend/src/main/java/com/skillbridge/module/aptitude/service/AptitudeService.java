package com.skillbridge.module.aptitude.service;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.aptitude.dto.*;
import org.springframework.data.domain.Page;

public interface AptitudeService {
    ApiResponse<Page<AptitudeQuestionResponse>> getQuestions(AptitudeCategory category, int page, int size);
    ApiResponse<QuizStartResponse> startQuiz(Long userId, QuizStartRequest request);
    ApiResponse<QuizResultResponse> submitQuiz(Long userId, QuizSubmitRequest request);
    ApiResponse<Page<QuizResultResponse>> getMyAttempts(Long userId, int page, int size);
    ApiResponse<AptitudeStatsResponse> getStats(Long userId);
    ApiResponse<java.util.List<String>> getWeakAreas(Long userId);
}
