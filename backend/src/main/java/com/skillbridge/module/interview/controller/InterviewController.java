package com.skillbridge.module.interview.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.interview.dto.*;
import com.skillbridge.module.interview.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/book")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<MockInterviewResponse>> bookSlot(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BookSlotRequest request) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.bookSlot(user.getId(), request)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<MockInterviewResponse>>> getMyInterviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.getMyInterviews(user.getId())));
    }

    @GetMapping("/officer")
    @PreAuthorize("hasAnyRole('PLACEMENT_OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<List<MockInterviewResponse>>> getOfficerInterviews(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.getOfficerInterviews(user.getId())));
    }

    @PutMapping("/{id}/feedback")
    @PreAuthorize("hasAnyRole('PLACEMENT_OFFICER','ADMIN')")
    public ResponseEntity<ApiResponse<MockInterviewResponse>> submitFeedback(
            @PathVariable Long id,
            @Valid @RequestBody InterviewFeedbackRequest request) {
        return ResponseEntity.ok(ApiResponse.success(interviewService.submitFeedback(id, request)));
    }
}
