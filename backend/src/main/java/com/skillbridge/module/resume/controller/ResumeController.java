package com.skillbridge.module.resume.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.resume.dto.ResumeUploadResponse;
import com.skillbridge.module.resume.service.ResumeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController @RequestMapping("/api/resume")
@RequiredArgsConstructor @Tag(name = "Resume") @PreAuthorize("hasRole('STUDENT')")
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> upload(@RequestParam("file") MultipartFile file, Authentication auth) {
        return ResponseEntity.ok(resumeService.uploadResume(((User) auth.getPrincipal()).getId(), file));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ResumeUploadResponse>>> getMyResumes(Authentication auth) {
        return ResponseEntity.ok(resumeService.getMyResumes(((User) auth.getPrincipal()).getId()));
    }

    @GetMapping("/my/latest")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> getLatest(Authentication auth) {
        return ResponseEntity.ok(resumeService.getLatestResume(((User) auth.getPrincipal()).getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(resumeService.deleteResume(((User) auth.getPrincipal()).getId(), id));
    }
}
