package com.skillbridge.module.company.service;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.company.dto.*;
import org.springframework.data.domain.Page;

public interface CompanyService {
    ApiResponse<Page<CompanyResponse>> getCompanies(String industry, int page, int size);
    ApiResponse<CompanyResponse> getCompany(Long id);
    ApiResponse<Page<CompanyResponse>> searchCompanies(String q, int page, int size);
    ApiResponse<CompanyResponse> addCompany(CompanyRequest request);
    ApiResponse<CompanyResponse> updateCompany(Long id, CompanyRequest request);
    ApiResponse<Void> deleteCompany(Long id);
    ApiResponse<CompanyRoundResponse> addRound(Long companyId, CompanyRoundRequest request);
    ApiResponse<CompanyRoundResponse> updateRound(Long companyId, Long roundId, CompanyRoundRequest request);
}
