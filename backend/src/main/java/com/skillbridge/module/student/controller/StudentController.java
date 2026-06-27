package com.skillbridge.module.student.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.student.dto.*;
import com.skillbridge.module.student.service.StudentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student")
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> getProfile(Authentication auth) {
        return ResponseEntity.ok(studentService.getProfile(getUser(auth).getId()));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<StudentProfileResponse>> updateProfile(
            @Valid @RequestBody StudentProfileRequest req, Authentication auth) {
        return ResponseEntity.ok(studentService.updateProfile(getUser(auth).getId(), req));
    }

    @PostMapping("/profile/photo")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<String>> uploadPhoto(@RequestParam("file") MultipartFile file, Authentication auth) {
        return ResponseEntity.ok(studentService.uploadPhoto(getUser(auth).getId(), file));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboard(Authentication auth) {
        return ResponseEntity.ok(studentService.getDashboardSummary(getUser(auth).getId()));
    }

    @GetMapping("/readiness-score")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<BigDecimal>> getReadinessScore(Authentication auth) {
        return ResponseEntity.ok(studentService.getReadinessScore(getUser(auth).getId()));
    }

    private User getUser(Authentication auth) { return (User) auth.getPrincipal(); }
}
