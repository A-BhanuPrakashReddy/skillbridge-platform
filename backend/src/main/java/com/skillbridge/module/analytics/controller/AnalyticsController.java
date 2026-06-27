package com.skillbridge.module.analytics.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.analytics.dto.*;
import com.skillbridge.module.analytics.service.AnalyticsService;
import com.skillbridge.module.auth.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dsa-growth")
    public ResponseEntity<ApiResponse<DSAGrowthResponse>> getDSAGrowth(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getDSAGrowth(user.getId())));
    }

    @GetMapping("/aptitude-growth")
    public ResponseEntity<ApiResponse<AptitudeGrowthResponse>> getAptitudeGrowth(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getAptitudeGrowth(user.getId())));
    }

    @GetMapping("/resume-history")
    public ResponseEntity<ApiResponse<ResumeScoreHistoryResponse>> getResumeHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getResumeScoreHistory(user.getId())));
    }

    @GetMapping("/readiness-trend")
    public ResponseEntity<ApiResponse<ReadinessTrendResponse>> getReadinessTrend(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getReadinessTrend(user.getId())));
    }
}
