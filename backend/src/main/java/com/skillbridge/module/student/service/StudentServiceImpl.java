package com.skillbridge.module.student.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.aptitude.repository.QuizAttemptRepository;
import com.skillbridge.module.company.repository.CompanyRepository;
import com.skillbridge.module.dsa.repository.DSAProblemRepository;
import com.skillbridge.module.dsa.repository.DSAStreakRepository;
import com.skillbridge.module.dsa.repository.UserDSAProgressRepository;
import com.skillbridge.module.interview.repository.MockInterviewRepository;
import com.skillbridge.module.resume.repository.ResumeUploadRepository;
import com.skillbridge.module.student.dto.*;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import com.skillbridge.module.readiness.service.ReadinessScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository profileRepository;
    private final UserDSAProgressRepository dsaProgressRepository;
    private final DSAProblemRepository dsaProblemRepository;
    private final DSAStreakRepository streakRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ResumeUploadRepository resumeUploadRepository;
    private final CompanyRepository companyRepository;
    private final MockInterviewRepository interviewRepository;
    private final ReadinessScoreService readinessScoreService;
    private final Cloudinary cloudinary;

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<StudentProfileResponse> getProfile(Long userId) {
        StudentProfile profile = findProfile(userId);
        return ApiResponse.success("Profile fetched", toResponse(profile));
    }

    @Override
    @Transactional
    public ApiResponse<StudentProfileResponse> updateProfile(Long userId, StudentProfileRequest request) {
        StudentProfile profile = findProfile(userId);
        if (request.getCollege() != null) profile.setCollege(request.getCollege());
        if (request.getBranch() != null) profile.setBranch(request.getBranch());
        if (request.getGraduationYear() != null) profile.setGraduationYear(request.getGraduationYear());
        if (request.getCgpa() != null) profile.setCgpa(request.getCgpa());
        if (request.getActiveBacklogs() != null) profile.setActiveBacklogs(request.getActiveBacklogs());
        if (request.getTotalBacklogs() != null) profile.setTotalBacklogs(request.getTotalBacklogs());
        if (request.getPhone() != null) profile.setPhone(request.getPhone());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getPortfolioUrl() != null) profile.setPortfolioUrl(request.getPortfolioUrl());
        if (request.getSkills() != null) profile.setSkillsList(request.getSkills());
        profileRepository.save(profile);
        readinessScoreService.computeAndSave(userId);
        return ApiResponse.success("Profile updated", toResponse(findProfile(userId)));
    }

    @Override
    @Transactional
    public ApiResponse<String> uploadPhoto(Long userId, MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "skillbridge/photos", "resource_type", "image"));
            String url = (String) uploadResult.get("secure_url");
            StudentProfile profile = findProfile(userId);
            profile.setPhotoUrl(url);
            profileRepository.save(profile);
            return ApiResponse.success("Photo uploaded", url);
        } catch (Exception e) {
            throw new BadRequestException("Failed to upload photo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DashboardSummaryResponse> getDashboardSummary(Long userId) {
        StudentProfile profile = findProfile(userId);
        Long totalSolved = dsaProgressRepository.countSolvedByUserId(userId);
        Long totalProblems = dsaProblemRepository.countByIsDeletedFalse();
        var streak = streakRepository.findByUserId(userId).orElse(null);
        Long totalAttempts = (long) quizAttemptRepository.findByUserIdOrderByAttemptedAtDesc(userId).size();
        Double avgApt = quizAttemptRepository.findAllAverageScore(userId);
        var latestResume = resumeUploadRepository.findByUserIdAndIsLatestTrue(userId);
        Long totalCompanies = companyRepository.countByIsDeletedFalseAndIsActiveTrue();
        Long pendingInterviews = interviewRepository.countPendingByStudent(userId);
        Long completedInterviews = interviewRepository.countCompletedByStudent(userId);
        Double avgInterview = interviewRepository.avgScoreByStudent(userId);

        DashboardSummaryResponse summary = DashboardSummaryResponse.builder()
                .name(profile.getUser().getName())
                .cgpa(profile.getCgpa())
                .branch(profile.getBranch())
                .readinessScore(profile.getReadinessScore())
                .totalSolved(totalSolved != null ? totalSolved : 0L)
                .totalProblems(totalProblems)
                .currentStreak(streak != null ? streak.getCurrentStreak() : 0)
                .longestStreak(streak != null ? streak.getLongestStreak() : 0)
                .avgAptitudeScore(avgApt != null ? avgApt : 0.0)
                .totalQuizAttempts(totalAttempts)
                .latestAtsScore(latestResume.map(r -> r.getAtsScore()).orElse(null))
                .hasResume(latestResume.isPresent())
                .eligibleCompaniesCount(0L)
                .totalCompaniesCount(totalCompanies)
                .pendingInterviews(pendingInterviews != null ? pendingInterviews : 0L)
                .completedInterviews(completedInterviews != null ? completedInterviews : 0L)
                .avgInterviewScore(avgInterview != null ? BigDecimal.valueOf(avgInterview) : null)
                .build();
        return ApiResponse.success("Dashboard fetched", summary);
    }

    @Override
    public ApiResponse<BigDecimal> getReadinessScore(Long userId) {
        BigDecimal score = readinessScoreService.computeAndSave(userId);
        return ApiResponse.success("Readiness score", score);
    }

    private StudentProfile findProfile(Long userId) {
        return profileRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
    }

    private StudentProfileResponse toResponse(StudentProfile p) {
        return StudentProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .name(p.getUser().getName())
                .email(p.getUser().getEmail())
                .college(p.getCollege())
                .branch(p.getBranch())
                .graduationYear(p.getGraduationYear())
                .cgpa(p.getCgpa())
                .activeBacklogs(p.getActiveBacklogs())
                .totalBacklogs(p.getTotalBacklogs())
                .phone(p.getPhone())
                .linkedinUrl(p.getLinkedinUrl())
                .githubUrl(p.getGithubUrl())
                .portfolioUrl(p.getPortfolioUrl())
                .skills(p.getSkillsList())
                .photoUrl(p.getPhotoUrl())
                .readinessScore(p.getReadinessScore())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
