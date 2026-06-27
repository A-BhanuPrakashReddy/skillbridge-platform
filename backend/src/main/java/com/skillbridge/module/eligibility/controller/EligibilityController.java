package com.skillbridge.module.eligibility.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.eligibility.dto.EligibilityCheckResponse;
import com.skillbridge.module.eligibility.service.EligibilityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/eligibility")
@RequiredArgsConstructor @PreAuthorize("hasRole('STUDENT')") @Tag(name = "Eligibility")
public class EligibilityController {
    private final EligibilityService eligibilityService;

    @GetMapping("/check/{companyId}")
    public ResponseEntity<ApiResponse<EligibilityCheckResponse>> check(@PathVariable Long companyId, Authentication auth) {
        return ResponseEntity.ok(eligibilityService.checkEligibility(((User) auth.getPrincipal()).getId(), companyId));
    }

    @GetMapping("/eligible-companies")
    public ResponseEntity<ApiResponse<List<EligibilityCheckResponse>>> eligibleCompanies(Authentication auth) {
        return ResponseEntity.ok(eligibilityService.getEligibleCompanies(((User) auth.getPrincipal()).getId()));
    }
}
