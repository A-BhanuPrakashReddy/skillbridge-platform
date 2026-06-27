package com.skillbridge.module.aptitude.controller;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.aptitude.dto.*;
import com.skillbridge.module.aptitude.service.AptitudeService;
import com.skillbridge.module.auth.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/aptitude")
@RequiredArgsConstructor @Tag(name = "Aptitude")
public class AptitudeController {
    private final AptitudeService aptitudeService;

    @GetMapping("/questions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<AptitudeQuestionResponse>>> getQuestions(
            @RequestParam AptitudeCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(aptitudeService.getQuestions(category, page, size));
    }

    @PostMapping("/quiz/start")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<QuizStartResponse>> startQuiz(@Valid @RequestBody QuizStartRequest req, Authentication auth) {
        return ResponseEntity.ok(aptitudeService.startQuiz(((User) auth.getPrincipal()).getId(), req));
    }

    @PostMapping("/quiz/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<QuizResultResponse>> submitQuiz(@Valid @RequestBody QuizSubmitRequest req, Authentication auth) {
        return ResponseEntity.ok(aptitudeService.submitQuiz(((User) auth.getPrincipal()).getId(), req));
    }

    @GetMapping("/attempts/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<QuizResultResponse>>> getMyAttempts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size, Authentication auth) {
        return ResponseEntity.ok(aptitudeService.getMyAttempts(((User) auth.getPrincipal()).getId(), page, size));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<AptitudeStatsResponse>> getStats(Authentication auth) {
        return ResponseEntity.ok(aptitudeService.getStats(((User) auth.getPrincipal()).getId()));
    }

    @GetMapping("/weak-areas")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<String>>> getWeakAreas(Authentication auth) {
        return ResponseEntity.ok(aptitudeService.getWeakAreas(((User) auth.getPrincipal()).getId()));
    }
}
