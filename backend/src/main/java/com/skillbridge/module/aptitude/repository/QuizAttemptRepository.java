package com.skillbridge.module.aptitude.repository;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.module.aptitude.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserIdOrderByAttemptedAtDesc(Long userId);
    Page<QuizAttempt> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT AVG(q.scorePercentage) FROM QuizAttempt q WHERE q.user.id = :userId AND q.category = :category")
    Double findAvgScoreByUserAndCategory(@Param("userId") Long userId, @Param("category") AptitudeCategory category);

    @Query("SELECT q.category, AVG(q.scorePercentage) FROM QuizAttempt q WHERE q.user.id = :userId GROUP BY q.category")
    List<Object[]> findAvgScoreByCategory(@Param("userId") Long userId);

    @Query(value = "SELECT DATE(attempted_at) as day, AVG(score_percentage) as avg_score FROM quiz_attempts WHERE user_id = :userId AND attempted_at >= :from GROUP BY DATE(attempted_at) ORDER BY day", nativeQuery = true)
    List<Object[]> findDailyAverageScore(@Param("userId") Long userId, @Param("from") LocalDateTime from);

    @Query("SELECT AVG(q.scorePercentage) FROM QuizAttempt q WHERE q.user.id = :userId")
    Double findAllAverageScore(@Param("userId") Long userId);

    // Alias used by OfficerServiceImpl and AnalyticsServiceImpl
    @Query("SELECT AVG(q.scorePercentage) FROM QuizAttempt q WHERE q.user.id = :userId")
    Double findAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(q) FROM QuizAttempt q WHERE q.user.id = :userId")
    Long countCompletedByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT YEARWEEK(attempted_at, 1) as week, AVG(score_percentage) as avg_score, COUNT(*) as attempts FROM quiz_attempts WHERE user_id = :userId GROUP BY YEARWEEK(attempted_at, 1) ORDER BY week", nativeQuery = true)
    List<Object[]> findWeeklyStats(@Param("userId") Long userId);
}
