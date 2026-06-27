package com.skillbridge.module.officer.service;

import com.skillbridge.module.officer.dto.*;
import org.springframework.data.domain.Page;

public interface OfficerService {
    Page<StudentSummaryResponse> getStudents(StudentFilterRequest filter);
    StudentDetailResponse getStudentDetail(Long studentId);
    OfficerAnalyticsResponse getAnalytics();
}
