package com.skillbridge.module.officer.service;

import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.module.aptitude.repository.QuizAttemptRepository;
import com.skillbridge.module.dsa.repository.UserDSAProgressRepository;
import com.skillbridge.module.interview.repository.MockInterviewRepository;
import com.skillbridge.module.officer.dto.*;
import com.skillbridge.module.resume.repository.ResumeUploadRepository;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfficerServiceImpl implements OfficerService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserDSAProgressRepository dsaProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final MockInterviewRepository interviewRepository;
    private final ResumeUploadRepository resumeUploadRepository;

    @Override
    public Page<StudentSummaryResponse> getStudents(StudentFilterRequest filter) {
        PageRequest pageable = PageRequest.of(filter.getPage(), filter.getSize());
        return studentProfileRepository.findAll(StudentSpecification.withFilters(filter), pageable)
                .map(this::toSummary);
    }

    @Override
    public StudentDetailResponse getStudentDetail(Long studentId) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Long userId = profile.getUser().getId();
        Long dsaSolved = dsaProgressRepository.countSolvedByUserId(userId);
        Long quizzesTaken = quizAttemptRepository.countCompletedByUserId(userId);
        Double avgAptitudeScore = quizAttemptRepository.findAverageScoreByUserId(userId);
        Long interviewsCompleted = interviewRepository.countCompletedByStudent(userId);
        Double avgInterviewScore = interviewRepository.avgScoreByStudent(userId);
        long resumesUploaded = resumeUploadRepository.countByUserId(userId);
        Double latestAtsScore = resumeUploadRepository.findLatestAtsByStudentProfileId(userId);

        return StudentDetailResponse.builder()
                .id(profile.getId())
                .name(profile.getUser().getName())
                .email(profile.getUser().getEmail())
                .branch(profile.getBranch())
                .cgpa(profile.getCgpa())
                .activeBacklogs(profile.getActiveBacklogs())
                .readinessScore(profile.getReadinessScore())
                .skills(profile.getSkills())
                .photoUrl(profile.getPhotoUrl())
                .dsaSolved(dsaSolved != null ? dsaSolved : 0L)
                .quizzesTaken(quizzesTaken != null ? quizzesTaken : 0L)
                .avgAptitudeScore(avgAptitudeScore != null ? avgAptitudeScore : 0.0)
                .interviewsCompleted(interviewsCompleted != null ? interviewsCompleted : 0L)
                .avgInterviewScore(avgInterviewScore != null ? avgInterviewScore : 0.0)
                .resumesUploaded(resumesUploaded)
                .latestAtsScore(latestAtsScore != null ? latestAtsScore : 0.0)
                .build();
    }

    @Override
    public OfficerAnalyticsResponse getAnalytics() {
        List<StudentProfile> all = studentProfileRepository.findAll();
        long total = all.size();
        double avg = all.stream()
                .mapToDouble(s -> s.getReadinessScore() != null ? s.getReadinessScore().doubleValue() : 0.0)
                .average().orElse(0.0);
        long above70 = all.stream()
                .filter(s -> s.getReadinessScore() != null && s.getReadinessScore().doubleValue() >= 70).count();
        long below50 = all.stream()
                .filter(s -> s.getReadinessScore() == null || s.getReadinessScore().doubleValue() < 50).count();

        Map<String, Long> branchCount = all.stream()
                .filter(s -> s.getBranch() != null)
                .collect(Collectors.groupingBy(StudentProfile::getBranch, Collectors.counting()));

        Map<String, Double> branchAvg = all.stream()
                .filter(s -> s.getBranch() != null && s.getReadinessScore() != null)
                .collect(Collectors.groupingBy(StudentProfile::getBranch,
                        Collectors.averagingDouble(s -> s.getReadinessScore().doubleValue())));

        return OfficerAnalyticsResponse.builder()
                .totalStudents(total)
                .avgReadinessScore(avg)
                .studentsAbove70(above70)
                .studentsBelow50(below50)
                .branchWiseCount(branchCount)
                .branchWiseAvgReadiness(branchAvg)
                .build();
    }

    private StudentSummaryResponse toSummary(StudentProfile p) {
        return StudentSummaryResponse.builder()
                .id(p.getId())
                .name(p.getUser().getName())
                .email(p.getUser().getEmail())
                .branch(p.getBranch())
                .cgpa(p.getCgpa())
                .activeBacklogs(p.getActiveBacklogs())
                .readinessScore(p.getReadinessScore())
                .skills(p.getSkills())
                .photoUrl(p.getPhotoUrl())
                .build();
    }
}
