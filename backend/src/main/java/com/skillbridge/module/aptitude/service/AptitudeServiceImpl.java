package com.skillbridge.module.aptitude.service;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.aptitude.dto.*;
import com.skillbridge.module.aptitude.entity.*;
import com.skillbridge.module.aptitude.repository.*;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.auth.repository.UserRepository;
import com.skillbridge.module.readiness.service.ReadinessScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class AptitudeServiceImpl implements AptitudeService {

    private final AptitudeQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAttemptDetailRepository detailRepository;
    private final UserRepository userRepository;
    private final ReadinessScoreService readinessScoreService;

    @Override
    public ApiResponse<Page<AptitudeQuestionResponse>> getQuestions(AptitudeCategory category, int page, int size) {
        Page<AptitudeQuestion> questions = questionRepository.findByCategoryAndIsDeletedFalse(category, PageRequest.of(page, size));
        return ApiResponse.success("Questions fetched", questions.map(this::toQuestionResponse));
    }

    @Override @Transactional
    public ApiResponse<QuizStartResponse> startQuiz(Long userId, QuizStartRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        int count = request.getQuestionCount() != null ? request.getQuestionCount() : 10;
        List<AptitudeQuestion> questions = questionRepository.findRandomByCategory(request.getCategory().name(), count);
        if (questions.isEmpty()) throw new BadRequestException("No questions available for this category");

        QuizAttempt attempt = QuizAttempt.builder()
                .user(user).category(request.getCategory())
                .totalQuestions(questions.size())
                .attemptedAt(LocalDateTime.now())
                .correctAnswers(0).wrongAnswers(0).build();
        attemptRepository.save(attempt);

        List<AptitudeQuestionResponse> qResponses = questions.stream().map(this::toQuestionResponse).collect(Collectors.toList());
        return ApiResponse.success("Quiz started", QuizStartResponse.builder()
                .attemptId(attempt.getId()).category(request.getCategory())
                .totalQuestions(questions.size()).questions(qResponses).build());
    }

    @Override @Transactional
    public ApiResponse<QuizResultResponse> submitQuiz(Long userId, QuizSubmitRequest request) {
        QuizAttempt attempt = attemptRepository.findById(request.getAttemptId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found"));
        if (!attempt.getUser().getId().equals(userId))
            throw new BadRequestException("Unauthorized quiz submission");

        int correct = 0, wrong = 0;
        List<QuizResultResponse.QuestionResultDTO> results = new ArrayList<>();

        for (QuizSubmitRequest.QuizAnswerDTO answer : request.getAnswers()) {
            AptitudeQuestion q = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found: " + answer.getQuestionId()));
            boolean isCorrect = q.getCorrectOption().equalsIgnoreCase(answer.getSelectedOption());
            if (isCorrect) correct++; else wrong++;

            QuizAttemptDetail detail = QuizAttemptDetail.builder()
                    .attempt(attempt).question(q)
                    .selectedOption(answer.getSelectedOption())
                    .isCorrect(isCorrect).build();
            detailRepository.save(detail);

            results.add(QuizResultResponse.QuestionResultDTO.builder()
                    .questionId(q.getId()).questionText(q.getQuestionText())
                    .optionA(q.getOptionA()).optionB(q.getOptionB())
                    .optionC(q.getOptionC()).optionD(q.getOptionD())
                    .selectedOption(answer.getSelectedOption())
                    .correctOption(q.getCorrectOption())
                    .isCorrect(isCorrect).explanation(q.getExplanation()).build());
        }

        BigDecimal score = attempt.getTotalQuestions() > 0
                ? BigDecimal.valueOf((double) correct / attempt.getTotalQuestions() * 100).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setScorePercentage(score);
        attempt.setTimeTakenSeconds(request.getTimeTakenSeconds());
        attemptRepository.save(attempt);
        readinessScoreService.computeAndSave(userId);

        return ApiResponse.success("Quiz submitted", QuizResultResponse.builder()
                .attemptId(attempt.getId()).category(attempt.getCategory())
                .totalQuestions(attempt.getTotalQuestions())
                .correctAnswers(correct).wrongAnswers(wrong)
                .scorePercentage(score).timeTakenSeconds(request.getTimeTakenSeconds())
                .questionResults(results).build());
    }

    @Override
    public ApiResponse<Page<QuizResultResponse>> getMyAttempts(Long userId, int page, int size) {
        Page<QuizAttempt> attempts = attemptRepository.findByUserId(userId, PageRequest.of(page, size));
        return ApiResponse.success("Attempts fetched", attempts.map(a -> QuizResultResponse.builder()
                .attemptId(a.getId()).category(a.getCategory())
                .totalQuestions(a.getTotalQuestions()).correctAnswers(a.getCorrectAnswers())
                .wrongAnswers(a.getWrongAnswers()).scorePercentage(a.getScorePercentage())
                .timeTakenSeconds(a.getTimeTakenSeconds()).build()));
    }

    @Override
    public ApiResponse<AptitudeStatsResponse> getStats(Long userId) {
        Double q = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.QUANTITATIVE);
        Double l = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.LOGICAL);
        Double v = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.VERBAL);
        List<Double> vals = Arrays.asList(q != null ? q : 0.0, l != null ? l : 0.0, v != null ? v : 0.0);
        double overall = vals.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        long total = attemptRepository.findByUserIdOrderByAttemptedAtDesc(userId).size();
        List<String> weak = new ArrayList<>(), strong = new ArrayList<>();
        if (q != null) { if (q < 50) weak.add("QUANTITATIVE"); else if (q >= 75) strong.add("QUANTITATIVE"); }
        if (l != null) { if (l < 50) weak.add("LOGICAL"); else if (l >= 75) strong.add("LOGICAL"); }
        if (v != null) { if (v < 50) weak.add("VERBAL"); else if (v >= 75) strong.add("VERBAL"); }
        return ApiResponse.success("Stats fetched", AptitudeStatsResponse.builder()
                .quantitativeAvg(q).logicalAvg(l).verbalAvg(v)
                .overallAvg(overall).totalAttempts(total)
                .weakAreas(weak).strongAreas(strong).build());
    }

    @Override
    public ApiResponse<List<String>> getWeakAreas(Long userId) {
        List<String> weak = new ArrayList<>();
        Double q = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.QUANTITATIVE);
        Double l = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.LOGICAL);
        Double v = attemptRepository.findAvgScoreByUserAndCategory(userId, AptitudeCategory.VERBAL);
        if (q == null || q < 50) weak.add("QUANTITATIVE");
        if (l == null || l < 50) weak.add("LOGICAL");
        if (v == null || v < 50) weak.add("VERBAL");
        return ApiResponse.success("Weak areas", weak);
    }

    private AptitudeQuestionResponse toQuestionResponse(AptitudeQuestion q) {
        return AptitudeQuestionResponse.builder().id(q.getId()).questionText(q.getQuestionText())
                .optionA(q.getOptionA()).optionB(q.getOptionB()).optionC(q.getOptionC()).optionD(q.getOptionD())
                .category(q.getCategory()).difficulty(q.getDifficulty()).build();
    }
}
