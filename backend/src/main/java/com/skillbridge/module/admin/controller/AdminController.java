package com.skillbridge.module.admin.controller;

import com.skillbridge.common.enums.Role;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.dto.RegisterRequest;
import com.skillbridge.module.auth.service.AuthService;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AuthService authService;
    private final StudentProfileRepository studentProfileRepository;

    @PostMapping("/officers")
    public ResponseEntity<ApiResponse<String>> createOfficer(@RequestBody RegisterRequest request) {
        request.setRole(Role.PLACEMENT_OFFICER);
        authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Officer account created"));
    }

    @GetMapping("/students/count")
    public ResponseEntity<ApiResponse<Long>> getStudentCount() {
        return ResponseEntity.ok(ApiResponse.success(studentProfileRepository.count()));
    }
}
