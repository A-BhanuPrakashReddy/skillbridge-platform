package com.skillbridge.module.dsa.controller;

import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.dsa.dto.DSAProblemRequest;
import com.skillbridge.module.dsa.dto.DSAProblemResponse;
import com.skillbridge.module.dsa.entity.DSAProblem;
import com.skillbridge.module.dsa.repository.DSAProblemRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dsa")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - DSA")
public class AdminDSAController {

    private final DSAProblemRepository problemRepository;

    @PostMapping("/problems")
    public ResponseEntity<ApiResponse<DSAProblemResponse>> addProblem(@Valid @RequestBody DSAProblemRequest req) {
        DSAProblem p = DSAProblem.builder()
                .title(req.getTitle()).topic(req.getTopic())
                .difficulty(req.getDifficulty()).platform(req.getPlatform())
                .problemUrl(req.getProblemUrl()).isDeleted(false).build();
        problemRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Problem added", toResponse(p)));
    }

    @PutMapping("/problems/{id}")
    public ResponseEntity<ApiResponse<DSAProblemResponse>> updateProblem(@PathVariable Long id, @Valid @RequestBody DSAProblemRequest req) {
        DSAProblem p = problemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        p.setTitle(req.getTitle()); p.setTopic(req.getTopic()); p.setDifficulty(req.getDifficulty());
        p.setPlatform(req.getPlatform()); p.setProblemUrl(req.getProblemUrl());
        problemRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Problem updated", toResponse(p)));
    }

    @DeleteMapping("/problems/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProblem(@PathVariable Long id) {
        DSAProblem p = problemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        p.setDeleted(true);
        problemRepository.save(p);
        return ResponseEntity.ok(ApiResponse.success("Problem deleted"));
    }

    private DSAProblemResponse toResponse(DSAProblem p) {
        return DSAProblemResponse.builder().id(p.getId()).title(p.getTitle()).topic(p.getTopic())
                .difficulty(p.getDifficulty()).platform(p.getPlatform()).problemUrl(p.getProblemUrl())
                .createdAt(p.getCreatedAt()).build();
    }
}
