package com.skillbridge.module.analytics.service;

import com.skillbridge.module.analytics.dto.*;
import com.skillbridge.module.aptitude.repository.QuizAttemptRepository;
import com.skillbridge.module.dsa.repository.UserDSAProgressRepository;
import com.skillbridge.module.resume.repository.ResumeUploadRepository;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserDSAProgressRepository dsaProgressRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ResumeUploadRepository resumeUploadRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    public DSAGrowthResponse getDSAGrowth(Long userId) {
        List<Object[]> raw = dsaProgressRepository.findWeeklyGrowth(userId);
        List<String> weeks = raw.stream().map(r -> String.valueOf(r[0])).collect(Collectors.toList());
        List<Long> solved = raw.stream().map(r -> ((Number) r[1]).longValue()).collect(Collectors.toList());
        return DSAGrowthResponse.builder().weeks(weeks).solved(solved).build();
    }

    @Override
    public AptitudeGrowthResponse getAptitudeGrowth(Long userId) {
        List<Object[]> raw = quizAttemptRepository.findWeeklyStats(userId);
        List<String> weeks = raw.stream().map(r -> String.valueOf(r[0])).collect(Collectors.toList());
        List<Double> avgScores = raw.stream().map(r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0).collect(Collectors.toList());
        List<Long> attempts = raw.stream().map(r -> ((Number) r[2]).longValue()).collect(Collectors.toList());
        return AptitudeGrowthResponse.builder().weeks(weeks).avgScores(avgScores).attempts(attempts).build();
    }

    @Override
    public ResumeScoreHistoryResponse getResumeScoreHistory(Long userId) {
        List<Object[]> raw = resumeUploadRepository.findScoreHistoryByStudentId(userId);
        if (raw.isEmpty()) {
            return ResumeScoreHistoryResponse.builder().dates(List.of()).scores(List.of()).build();
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd");
        List<String> dates = raw.stream().map(r -> {
            LocalDateTime dt = (LocalDateTime) r[0];
            return dt.format(fmt);
        }).collect(Collectors.toList());
        List<Double> scores = raw.stream().map(r -> r[1] != null ? ((Number) r[1]).doubleValue() : 0.0).collect(Collectors.toList());
        return ResumeScoreHistoryResponse.builder().dates(dates).scores(scores).build();
    }

    @Override
    public ReadinessTrendResponse getReadinessTrend(Long userId) {
        Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(userId);
        double current = profileOpt.map(p -> p.getReadinessScore() != null ? p.getReadinessScore().doubleValue() : 0.0).orElse(0.0);
        List<String> dates = List.of("Week 1", "Week 2", "Week 3", "Week 4");
        List<Double> scores = List.of(current * 0.6, current * 0.75, current * 0.9, current);
        return ReadinessTrendResponse.builder()
                .dates(dates)
                .scores(scores)
                .currentScore(current)
                .improvement(current * 0.4)
                .build();
    }
}
