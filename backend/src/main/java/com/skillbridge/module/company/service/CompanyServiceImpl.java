package com.skillbridge.module.company.service;

import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.company.dto.*;
import com.skillbridge.module.company.entity.*;
import com.skillbridge.module.company.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyRoundRepository roundRepository;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Page<CompanyResponse>> getCompanies(String industry, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Company> companies = industry != null
                ? companyRepository.findByIndustryAndIsDeletedFalse(industry, pageable)
                : companyRepository.findByIsDeletedFalseAndIsActiveTrue(pageable);
        return ApiResponse.success("Companies fetched", companies.map(this::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CompanyResponse> getCompany(Long id) {
        Company c = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return ApiResponse.success("Company fetched", toResponse(c));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<Page<CompanyResponse>> searchCompanies(String q, int page, int size) {
        Page<Company> result = companyRepository.findByNameContainingIgnoreCaseAndIsDeletedFalse(q, PageRequest.of(page, size));
        return ApiResponse.success("Search results", result.map(this::toResponse));
    }

    @Override @Transactional
    public ApiResponse<CompanyResponse> addCompany(CompanyRequest request) {
        if (companyRepository.existsByNameIgnoreCase(request.getName()))
            throw new BadRequestException("Company already exists");
        Company c = buildCompany(new Company(), request);
        companyRepository.save(c);
        return ApiResponse.success("Company added", toResponse(c));
    }

    @Override @Transactional
    public ApiResponse<CompanyResponse> updateCompany(Long id, CompanyRequest request) {
        Company c = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        buildCompany(c, request);
        companyRepository.save(c);
        return ApiResponse.success("Company updated", toResponse(c));
    }

    @Override @Transactional
    public ApiResponse<Void> deleteCompany(Long id) {
        Company c = companyRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        c.setIsDeleted(true); companyRepository.save(c);
        return ApiResponse.success("Company deleted");
    }

    @Override @Transactional
    public ApiResponse<CompanyRoundResponse> addRound(Long companyId, CompanyRoundRequest request) {
        Company c = companyRepository.findByIdAndIsDeletedFalse(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        CompanyRound round = CompanyRound.builder().company(c)
                .roundNumber(request.getRoundNumber()).roundName(request.getRoundName())
                .roundType(request.getRoundType()).description(request.getDescription())
                .dsaTopics(listToComma(request.getDsaTopics()))
                .aptitudeTopics(listToComma(request.getAptitudeTopics()))
                .tips(request.getTips()).durationMinutes(request.getDurationMinutes())
                .isDeleted(false).build();
        roundRepository.save(round);
        return ApiResponse.success("Round added", toRoundResponse(round));
    }

    @Override @Transactional
    public ApiResponse<CompanyRoundResponse> updateRound(Long companyId, Long roundId, CompanyRoundRequest request) {
        CompanyRound round = roundRepository.findById(roundId)
                .orElseThrow(() -> new ResourceNotFoundException("Round not found"));
        round.setRoundNumber(request.getRoundNumber()); round.setRoundName(request.getRoundName());
        round.setRoundType(request.getRoundType()); round.setDescription(request.getDescription());
        round.setDsaTopics(listToComma(request.getDsaTopics()));
        round.setAptitudeTopics(listToComma(request.getAptitudeTopics()));
        round.setTips(request.getTips()); round.setDurationMinutes(request.getDurationMinutes());
        roundRepository.save(round);
        return ApiResponse.success("Round updated", toRoundResponse(round));
    }

    private Company buildCompany(Company c, CompanyRequest r) {
        c.setName(r.getName()); c.setIndustry(r.getIndustry()); c.setDescription(r.getDescription());
        c.setMinCgpa(r.getMinCgpa()); c.setMaxBacklogs(r.getMaxBacklogs() != null ? r.getMaxBacklogs() : 0);
        c.setRequiredSkills(listToComma(r.getRequiredSkills())); c.setPackageLpa(r.getPackageLpa());
        c.setBondYears(r.getBondYears() != null ? r.getBondYears() : 0);
        c.setLogoUrl(r.getLogoUrl()); c.setWebsiteUrl(r.getWebsiteUrl());
        c.setAptitudeTopics(listToComma(r.getAptitudeTopics()));
        c.setPreparationRoadmap(r.getPreparationRoadmap()); c.setInterviewTips(r.getInterviewTips());
        c.setPlacementHistory(r.getPlacementHistory());
        if (c.getIsActive() == null) c.setIsActive(true);
        if (c.getIsDeleted() == null) c.setIsDeleted(false);
        return c;
    }

    private String listToComma(List<String> list) {
        return (list != null && !list.isEmpty()) ? String.join(",", list) : null;
    }

    private List<String> commaToList(String s) {
        if (s == null || s.isBlank()) return List.of();
        return Arrays.asList(s.split(","));
    }

    private CompanyResponse toResponse(Company c) {
        return CompanyResponse.builder().id(c.getId()).name(c.getName()).industry(c.getIndustry())
                .description(c.getDescription()).minCgpa(c.getMinCgpa()).maxBacklogs(c.getMaxBacklogs())
                .requiredSkills(commaToList(c.getRequiredSkills())).packageLpa(c.getPackageLpa())
                .bondYears(c.getBondYears()).logoUrl(c.getLogoUrl()).websiteUrl(c.getWebsiteUrl())
                .aptitudeTopics(commaToList(c.getAptitudeTopics()))
                .preparationRoadmap(c.getPreparationRoadmap()).interviewTips(c.getInterviewTips())
                .placementHistory(c.getPlacementHistory()).isActive(c.getIsActive())
                .rounds(c.getRounds().stream().filter(r -> !Boolean.TRUE.equals(r.getIsDeleted()))
                        .map(this::toRoundResponse).collect(Collectors.toList()))
                .createdAt(c.getCreatedAt()).build();
    }

    private CompanyRoundResponse toRoundResponse(CompanyRound r) {
        return CompanyRoundResponse.builder().id(r.getId()).roundNumber(r.getRoundNumber())
                .roundName(r.getRoundName()).roundType(r.getRoundType()).description(r.getDescription())
                .dsaTopics(commaToList(r.getDsaTopics())).aptitudeTopics(commaToList(r.getAptitudeTopics()))
                .tips(r.getTips()).durationMinutes(r.getDurationMinutes()).build();
    }
}
