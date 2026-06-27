package com.skillbridge.module.dsa.controller;

import com.skillbridge.common.enums.Difficulty;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.dsa.dto.*;
import com.skillbridge.module.dsa.service.DSAService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/dsa")
@RequiredArgsConstructor
@Tag(name = "DSA Tracker")
public class DSAController {

    private final DSAService dsaService;

    @GetMapping("/problems")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Page<DSAProblemResponse>>> getProblems(
            @RequestParam(required = false) String topic,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long userId = auth != null ? ((User) auth.getPrincipal()).getId() : null;
        return ResponseEntity.ok(dsaService.getProblems(topic, difficulty, page, size, userId));
    }

    @GetMapping("/problems/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<DSAProblemResponse>> getProblem(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(dsaService.getProblem(id, ((User) auth.getPrincipal()).getId()));
    }

    @GetMapping("/topics")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<String>>> getTopics() {
        return ResponseEntity.ok(dsaService.getAllTopics());
    }

    @PostMapping("/progress")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DSAProgressResponse>> markProgress(
            @Valid @RequestBody MarkProgressRequest req, Authentication auth) {
        return ResponseEntity.ok(dsaService.markProgress(((User) auth.getPrincipal()).getId(), req));
    }

    @GetMapping("/progress/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<Page<DSAProgressResponse>>> getMyProgress(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        return ResponseEntity.ok(dsaService.getMyProgress(((User) auth.getPrincipal()).getId(), page, size));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DSAStatsResponse>> getStats(Authentication auth) {
        return ResponseEntity.ok(dsaService.getStats(((User) auth.getPrincipal()).getId()));
    }

    @GetMapping("/streak")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<DSAStreakResponse>> getStreak(Authentication auth) {
        return ResponseEntity.ok(dsaService.getStreak(((User) auth.getPrincipal()).getId()));
    }

    @GetMapping("/weak-topics")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<String>>> getWeakTopics(Authentication auth) {
        return ResponseEntity.ok(dsaService.getWeakTopics(((User) auth.getPrincipal()).getId()));
    }
}
