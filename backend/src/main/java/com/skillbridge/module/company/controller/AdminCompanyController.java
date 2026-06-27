package com.skillbridge.module.company.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.company.dto.*;
import com.skillbridge.module.company.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/admin/companies")
@RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')") @Tag(name = "Admin - Companies")
public class AdminCompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponse>> add(@Valid @RequestBody CompanyRequest req) {
        return ResponseEntity.ok(companyService.addCompany(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> update(@PathVariable Long id, @Valid @RequestBody CompanyRequest req) {
        return ResponseEntity.ok(companyService.updateCompany(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.deleteCompany(id));
    }

    @PostMapping("/{id}/rounds")
    public ResponseEntity<ApiResponse<CompanyRoundResponse>> addRound(@PathVariable Long id, @Valid @RequestBody CompanyRoundRequest req) {
        return ResponseEntity.ok(companyService.addRound(id, req));
    }

    @PutMapping("/{companyId}/rounds/{roundId}")
    public ResponseEntity<ApiResponse<CompanyRoundResponse>> updateRound(@PathVariable Long companyId, @PathVariable Long roundId, @Valid @RequestBody CompanyRoundRequest req) {
        return ResponseEntity.ok(companyService.updateRound(companyId, roundId, req));
    }
}
