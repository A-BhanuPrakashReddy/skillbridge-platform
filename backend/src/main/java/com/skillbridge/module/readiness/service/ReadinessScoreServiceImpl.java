package com.skillbridge.module.readiness.service;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.module.aptitude.repository.QuizAttemptRepository;
import com.skillbridge.module.dsa.repository.DSAProblemRepository;
import com.skillbridge.module.dsa.repository.UserDSAProgressRepository;
import com.skillbridge.module.interview.repository.MockInterviewRepository;
import com.skillbridge.module.resume.repository.ResumeUploadRepository;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReadinessScoreServiceImpl implements ReadinessScoreService {

    private final StudentProfileRepository profileRepository;
    private final UserDSAProgressRepository dsaProgressRepository;
    private final DSAProblemRepository dsaProblemRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ResumeUploadRepository resumeUploadRepository;
    private final MockInterviewRepository interviewRepository;

    @Override
    @Transactional
    public BigDecimal computeAndSave(Long userId) {
        StudentProfile profile = profileRepository.findByUserIdAndIsDeletedFalse(userId).orElse(null);
        if (profile == null) return BigDecimal.ZERO;

        double dsaScore = computeDsaScore(userId);
        double aptitudeScore = computeAptitudeScore(userId);
        double resumeScore = computeResumeScore(userId);
        double profileScore = computeProfileScore(profile);
        double interviewScore = computeInterviewScore(userId);

        double total = (dsaScore * 0.30) + (aptitudeScore * 0.25) + (resumeScore * 0.20) + (profileScore * 0.15) + (interviewScore * 0.10);
        BigDecimal readiness = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
        profile.setReadinessScore(readiness);
        profileRepository.save(profile);
        return readiness;
    }

    @Override
    public String getCategory(BigDecimal score) {
        double v = score.doubleValue();
        if (v >= 70) return "STRONG";
        if (v >= 40) return "AVERAGE";
        return "WEAK";
    }

    private double computeDsaScore(Long userId) {
        Long solved = dsaProgressRepository.countSolvedByUserId(userId);
        long total = dsaProblemRepository.countByIsDeletedFalse();
        if (total == 0) return 0.0;
        return (double) (solved != null ? solved : 0) / total * 100;
    }

    private double computeAptitudeScore(Long userId) {
        Double q = quizAttemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.QUANTITATIVE);
        Double l = quizAttemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.LOGICAL);
        Double v = quizAttemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.VERBAL);
        double sum = (q != null ? q : 0) + (l != null ? l : 0) + (v != null ? v : 0);
        long count = (q != null ? 1 : 0) + (l != null ? 1 : 0) + (v != null ? 1 : 0);
        return count > 0 ? sum / count : 0;
    }

    private double computeResumeScore(Long userId) {
        return resumeUploadRepository.findByUserIdAndIsLatestTrue(userId)
                .map(r -> r.getAtsScore() != null ? r.getAtsScore().doubleValue() : 0.0).orElse(0.0);
    }

    private double computeProfileScore(StudentProfile p) {
        int score = 0;
        if (p.getCollege() != null && !p.getCollege().isBlank()) score += 16;
        if (p.getBranch() != null && !p.getBranch().isBlank()) score += 16;
        if (p.getCgpa() != null) score += 16;
        if (p.getSkills() != null && p.getSkillsList().size() >= 3) score += 16;
        if (p.getPhotoUrl() != null && !p.getPhotoUrl().isBlank()) score += 16;
        if (p.getLinkedinUrl() != null && !p.getLinkedinUrl().isBlank()) score += 10;
        if (p.getGithubUrl() != null && !p.getGithubUrl().isBlank()) score += 10;
        return Math.min(score, 100);
    }

    private double computeInterviewScore(Long userId) {
        Double avg = interviewRepository.avgScoreByStudent(userId);
        return avg != null ? avg * 10 : 0;
    }
}
