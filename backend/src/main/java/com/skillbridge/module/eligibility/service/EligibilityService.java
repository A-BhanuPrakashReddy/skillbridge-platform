package com.skillbridge.module.eligibility.service;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.eligibility.dto.EligibilityCheckResponse;
import java.util.List;

public interface EligibilityService {
    ApiResponse<EligibilityCheckResponse> checkEligibility(Long userId, Long companyId);
    ApiResponse<List<EligibilityCheckResponse>> getEligibleCompanies(Long userId);
}
