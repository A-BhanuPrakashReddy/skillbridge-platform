package com.skillbridge.module.eligibility.service;

import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.company.entity.Company;
import com.skillbridge.module.company.repository.CompanyRepository;
import com.skillbridge.module.dsa.repository.DSAProblemRepository;
import com.skillbridge.module.dsa.repository.UserDSAProgressRepository;
import com.skillbridge.module.eligibility.dto.EligibilityCheckResponse;
import com.skillbridge.module.eligibility.dto.EligibilityCheckResponse.EligibilityCriterion;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class EligibilityServiceImpl implements EligibilityService {

    private final StudentProfileRepository profileRepository;
    private final CompanyRepository companyRepository;
    private final UserDSAProgressRepository dsaProgressRepository;
    private final DSAProblemRepository dsaProblemRepository;

    @Override
    public ApiResponse<EligibilityCheckResponse> checkEligibility(Long userId, Long companyId) {
        StudentProfile profile = profileRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        Company company = companyRepository.findByIdAndIsDeletedFalse(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found"));
        return ApiResponse.success("Eligibility checked", buildResponse(profile, company));
    }

    @Override
    public ApiResponse<List<EligibilityCheckResponse>> getEligibleCompanies(Long userId) {
        StudentProfile profile = profileRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        List<Company> companies = companyRepository.findByIsDeletedFalseAndIsActiveTrue();
        List<EligibilityCheckResponse> results = companies.stream()
                .map(c -> buildResponse(profile, c))
                .filter(EligibilityCheckResponse::getIsEligible)
                .collect(Collectors.toList());
        return ApiResponse.success("Eligible companies", results);
    }

    private EligibilityCheckResponse buildResponse(StudentProfile profile, Company company) {
        List<EligibilityCriterion> criteria = new ArrayList<>();
        int passed = 0;

        // CGPA
        boolean cgpaOk = profile.getCgpa() != null && company.getMinCgpa() != null
                && profile.getCgpa().compareTo(company.getMinCgpa()) >= 0;
        if (profile.getCgpa() == null) cgpaOk = company.getMinCgpa() == null;
        criteria.add(EligibilityCriterion.builder().criterionName("CGPA").isPassed(cgpaOk)
                .required(company.getMinCgpa() != null ? company.getMinCgpa() + " minimum" : "None")
                .actual(profile.getCgpa() != null ? profile.getCgpa().toString() : "Not set")
                .message(cgpaOk ? "You meet the CGPA requirement" : "CGPA below requirement").build());
        if (cgpaOk) passed++;

        // Backlogs
        boolean backlogOk = profile.getActiveBacklogs() != null && company.getMaxBacklogs() != null
                && profile.getActiveBacklogs() <= company.getMaxBacklogs();
        criteria.add(EligibilityCriterion.builder().criterionName("Backlogs").isPassed(backlogOk)
                .required("Max " + (company.getMaxBacklogs() != null ? company.getMaxBacklogs() : 0))
                .actual(String.valueOf(profile.getActiveBacklogs() != null ? profile.getActiveBacklogs() : 0))
                .message(backlogOk ? "Backlogs within limit" : "Too many active backlogs").build());
        if (backlogOk) passed++;

        // Skills
        boolean skillsOk = true;
        String skillMsg = "No skill requirements";
        if (company.getRequiredSkills() != null && !company.getRequiredSkills().isBlank()) {
            List<String> required = Arrays.asList(company.getRequiredSkills().toLowerCase().split(","));
            List<String> studentSkills = profile.getSkillsList().stream()
                    .map(String::toLowerCase).map(String::trim).collect(Collectors.toList());
            long matched = required.stream().filter(r -> studentSkills.contains(r.trim())).count();
            skillsOk = matched >= required.size() * 0.6;
            skillMsg = skillsOk ? "Skills match (" + matched + "/" + required.size() + ")"
                    : "Skills below 60% match (" + matched + "/" + required.size() + ")";
        }
        criteria.add(EligibilityCriterion.builder().criterionName("Skills").isPassed(skillsOk)
                .required(company.getRequiredSkills() != null ? company.getRequiredSkills() : "None")
                .actual(String.join(",", profile.getSkillsList())).message(skillMsg).build());
        if (skillsOk) passed++;

        // DSA
        Long solved = dsaProgressRepository.countSolvedByUserId(profile.getUser().getId());
        long total = dsaProblemRepository.countByIsDeletedFalse();
        double dsaProgress = total > 0 ? (double)(solved != null ? solved : 0) / total * 100 : 0;
        boolean dsaOk = dsaProgress >= 30;
        criteria.add(EligibilityCriterion.builder().criterionName("DSA").isPassed(dsaOk)
                .required("30% minimum").actual(String.format("%.1f%%", dsaProgress))
                .message(dsaOk ? "DSA progress sufficient" : "Need more DSA practice").build());
        if (dsaOk) passed++;

        int matchScore = (int)((double) passed / 4 * 100);
        return EligibilityCheckResponse.builder()
                .companyId(company.getId()).companyName(company.getName())
                .isEligible(passed == 4).criteria(criteria).matchScore(matchScore).build();
    }
}
