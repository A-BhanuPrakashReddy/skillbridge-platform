package com.skillbridge.module.dsa.service;

import com.skillbridge.common.enums.Difficulty;
import com.skillbridge.common.enums.DSAStatus;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.auth.repository.UserRepository;
import com.skillbridge.module.dsa.dto.*;
import com.skillbridge.module.dsa.entity.DSAProblem;
import com.skillbridge.module.dsa.entity.DSAStreak;
import com.skillbridge.module.dsa.entity.UserDSAProgress;
import com.skillbridge.module.dsa.repository.*;
import com.skillbridge.module.readiness.service.ReadinessScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DSAServiceImpl implements DSAService {

    private final DSAProblemRepository problemRepository;
    private final UserDSAProgressRepository progressRepository;
    private final DSAStreakRepository streakRepository;
    private final UserRepository userRepository;
    private final ReadinessScoreService readinessScoreService;

    @Override
    public ApiResponse<Page<DSAProblemResponse>> getProblems(
            String topic,
            Difficulty difficulty,
            int page,
            int size,
            Long userId) {

        Pageable pageable = PageRequest.of(page, size);
        Page<DSAProblem> problems;

        // Treat empty topic as "All Topics"
        if (topic != null && topic.trim().isEmpty()) {
            topic = null;
        }

        if (topic != null && difficulty != null) {
            problems = problemRepository.findByTopicAndDifficultyAndIsDeletedFalse(
                    topic, difficulty, pageable);

        } else if (topic != null) {
            problems = problemRepository.findByTopicAndIsDeletedFalse(
                    topic, pageable);

        } else if (difficulty != null) {
            problems = problemRepository.findByDifficultyAndIsDeletedFalse(
                    difficulty, pageable);

        } else {
            // All Topics + All Difficulty
            problems = problemRepository.findByIsDeletedFalse(pageable);
        }

        Map<Long, DSAStatus> userProgress = new HashMap<>();

        if (userId != null) {
            progressRepository.findByUserId(userId)
                    .forEach(p -> userProgress.put(
                            p.getProblem().getId(),
                            p.getStatus()));
        }

        return ApiResponse.success(
                "Problems fetched",
                problems.map(p -> toProblemResponse(
                        p,
                        userProgress.get(p.getId())))
        );
    }

    @Override
    public ApiResponse<DSAProblemResponse> getProblem(Long id, Long userId) {
        DSAProblem problem = problemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        DSAStatus status = progressRepository.findByUserIdAndProblemId(userId, id).map(UserDSAProgress::getStatus).orElse(null);
        return ApiResponse.success("Problem fetched", toProblemResponse(problem, status));
    }

    @Override
    public ApiResponse<List<String>> getAllTopics() {
        return ApiResponse.success("Topics fetched", problemRepository.findAllTopics());
    }

    @Override
    @Transactional
    public ApiResponse<DSAProgressResponse> markProgress(Long userId, MarkProgressRequest request) {
        DSAProblem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<UserDSAProgress> existing = progressRepository.findByUserIdAndProblemId(userId, request.getProblemId());
        UserDSAProgress progress;
        if (existing.isPresent()) {
            progress = existing.get();
        } else {
            progress = UserDSAProgress.builder().user(user).problem(problem).build();
        }
        progress.setStatus(request.getStatus());
        progress.setNotes(request.getNotes());
        progress.setTimeTakenMins(request.getTimeTakenMins());
        if (request.getStatus() == DSAStatus.SOLVED) {
            progress.setSolvedAt(LocalDate.now());
            updateStreak(userId, user);
        }
        progressRepository.save(progress);
        readinessScoreService.computeAndSave(userId);
        return ApiResponse.success("Progress marked", toProgressResponse(progress));
    }

    @Override
    public ApiResponse<Page<DSAProgressResponse>> getMyProgress(Long userId, int page, int size) {
        List<UserDSAProgress> all = progressRepository.findByUserId(userId);
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<DSAProgressResponse> paged = start >= all.size() ? new ArrayList<>() :
                all.subList(start, end).stream().map(this::toProgressResponse).collect(Collectors.toList());
        return ApiResponse.success("Progress fetched", new org.springframework.data.domain.PageImpl<>(paged, PageRequest.of(page, size), all.size()));
    }

    @Override
    public ApiResponse<DSAStatsResponse> getStats(Long userId) {
        Long solved = progressRepository.countSolvedByUserId(userId);
        Long attempted = progressRepository.countAttemptedByUserId(userId);
        Long revisit = progressRepository.countRevisitByUserId(userId);

        List<DSAProblem> allProblems = problemRepository.findByIsDeletedFalse();
        long easy = allProblems.stream().filter(p -> p.getDifficulty() == Difficulty.EASY).count();
        long medium = allProblems.stream().filter(p -> p.getDifficulty() == Difficulty.MEDIUM).count();
        long hard = allProblems.stream().filter(p -> p.getDifficulty() == Difficulty.HARD).count();

        List<Object[]> solvedByTopicRaw = progressRepository.countSolvedByTopic(userId);
        Map<String, Long> solvedByTopic = new LinkedHashMap<>();
        solvedByTopicRaw.forEach(row -> solvedByTopic.put((String) row[0], (Long) row[1]));

        Map<String, Long> totalByTopic = new LinkedHashMap<>();
        allProblems.forEach(p -> totalByTopic.merge(p.getTopic(), 1L, Long::sum));

        List<String> weakTopics = totalByTopic.keySet().stream()
                .filter(t -> solvedByTopic.getOrDefault(t, 0L) < 3)
                .collect(Collectors.toList());

        return ApiResponse.success("Stats fetched", DSAStatsResponse.builder()
                .totalSolved(solved != null ? solved : 0L)
                .totalAttempted(attempted != null ? attempted : 0L)
                .totalRevisit(revisit != null ? revisit : 0L)
                .easyCount(easy).mediumCount(medium).hardCount(hard)
                .solvedByTopic(solvedByTopic).totalByTopic(totalByTopic)
                .weakTopics(weakTopics).build());
    }

    @Override
    public ApiResponse<DSAStreakResponse> getStreak(Long userId) {
        Optional<DSAStreak> streak = streakRepository.findByUserId(userId);
        boolean solvedToday = streak.map(s -> LocalDate.now().equals(s.getLastSolvedDate())).orElse(false);
        return ApiResponse.success("Streak fetched", DSAStreakResponse.builder()
                .currentStreak(streak.map(DSAStreak::getCurrentStreak).orElse(0))
                .longestStreak(streak.map(DSAStreak::getLongestStreak).orElse(0))
                .lastSolvedDate(streak.map(DSAStreak::getLastSolvedDate).orElse(null))
                .solvedToday(solvedToday).build());
    }

    @Override
    public ApiResponse<List<String>> getWeakTopics(Long userId) {
        List<Object[]> solvedByTopicRaw = progressRepository.countSolvedByTopic(userId);
        Map<String, Long> solvedByTopic = new HashMap<>();
        solvedByTopicRaw.forEach(row -> solvedByTopic.put((String) row[0], (Long) row[1]));
        List<String> allTopics = problemRepository.findAllTopics();
        List<String> weak = allTopics.stream()
                .filter(t -> solvedByTopic.getOrDefault(t, 0L) < 3)
                .collect(Collectors.toList());
        return ApiResponse.success("Weak topics", weak);
    }

    private void updateStreak(Long userId, User user) {
        DSAStreak streak = streakRepository.findByUserId(userId)
                .orElse(DSAStreak.builder().user(user).currentStreak(0).longestStreak(0).build());
        LocalDate today = LocalDate.now();
        LocalDate last = streak.getLastSolvedDate();
        if (last == null) {
            streak.setCurrentStreak(1);
        } else if (last.equals(today)) {
            return;
        } else if (last.equals(today.minusDays(1))) {
            streak.setCurrentStreak(streak.getCurrentStreak() + 1);
        } else {
            streak.setCurrentStreak(1);
        }
        if (streak.getCurrentStreak() > streak.getLongestStreak()) {
            streak.setLongestStreak(streak.getCurrentStreak());
        }
        streak.setLastSolvedDate(today);
        streakRepository.save(streak);
    }

    private DSAProblemResponse toProblemResponse(DSAProblem p, DSAStatus status) {
        return DSAProblemResponse.builder()
                .id(p.getId()).title(p.getTitle()).topic(p.getTopic())
                .difficulty(p.getDifficulty()).platform(p.getPlatform())
                .problemUrl(p.getProblemUrl()).userStatus(status)
                .createdAt(p.getCreatedAt()).build();
    }

    private DSAProgressResponse toProgressResponse(UserDSAProgress p) {
        return DSAProgressResponse.builder()
                .id(p.getId()).problemId(p.getProblem().getId())
                .problemTitle(p.getProblem().getTitle()).topic(p.getProblem().getTopic())
                .status(p.getStatus()).notes(p.getNotes())
                .timeTakenMins(p.getTimeTakenMins()).solvedAt(p.getSolvedAt())
                .updatedAt(p.getUpdatedAt()).build();
    }
}
