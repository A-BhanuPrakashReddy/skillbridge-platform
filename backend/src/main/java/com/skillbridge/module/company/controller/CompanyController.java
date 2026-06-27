package com.skillbridge.module.company.controller;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.company.dto.CompanyResponse;
import com.skillbridge.module.company.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/companies") @RequiredArgsConstructor @Tag(name = "Companies")
public class CompanyController {
    private final CompanyService companyService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> getCompanies(
            @RequestParam(required = false) String industry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(companyService.getCompanies(industry, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CompanyResponse>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(companyService.searchCompanies(q, page, size));
    }
}
