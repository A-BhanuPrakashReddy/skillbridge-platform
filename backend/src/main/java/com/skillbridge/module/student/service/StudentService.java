package com.skillbridge.module.student.service;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.student.dto.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public interface StudentService {
    ApiResponse<StudentProfileResponse> getProfile(Long userId);
    ApiResponse<StudentProfileResponse> updateProfile(Long userId, StudentProfileRequest request);
    ApiResponse<String> uploadPhoto(Long userId, MultipartFile file);
    ApiResponse<DashboardSummaryResponse> getDashboardSummary(Long userId);
    ApiResponse<BigDecimal> getReadinessScore(Long userId);
}
