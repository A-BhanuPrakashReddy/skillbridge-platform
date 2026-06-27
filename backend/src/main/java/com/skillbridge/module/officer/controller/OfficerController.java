package com.skillbridge.module.officer.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.officer.dto.*;
import com.skillbridge.module.officer.service.OfficerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/officer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PLACEMENT_OFFICER','ADMIN')")
public class OfficerController {

    private final OfficerService officerService;

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<Page<StudentSummaryResponse>>> getStudents(StudentFilterRequest filter) {
        return ResponseEntity.ok(ApiResponse.success(officerService.getStudents(filter)));
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentDetail(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(officerService.getStudentDetail(studentId)));
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<OfficerAnalyticsResponse>> getAnalytics() {
        return ResponseEntity.ok(ApiResponse.success(officerService.getAnalytics()));
    }
}
