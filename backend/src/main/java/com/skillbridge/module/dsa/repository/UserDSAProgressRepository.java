package com.skillbridge.module.dsa.repository;

import com.skillbridge.module.dsa.entity.UserDSAProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDSAProgressRepository extends JpaRepository<UserDSAProgress, Long> {
    Optional<UserDSAProgress> findByUserIdAndProblemId(Long userId, Long problemId);
    List<UserDSAProgress> findByUserId(Long userId);

    @Query("SELECT COUNT(p) FROM UserDSAProgress p WHERE p.user.id = :userId AND p.status = 'SOLVED'")
    Long countSolvedByUserId(@Param("userId") Long userId);

    @Query("SELECT p.problem.topic, COUNT(p) FROM UserDSAProgress p WHERE p.user.id = :userId AND p.status = 'SOLVED' GROUP BY p.problem.topic")
    List<Object[]> countSolvedByTopic(@Param("userId") Long userId);

    @Query(value = "SELECT YEARWEEK(solved_at, 1) as week, COUNT(*) as count FROM user_dsa_progress WHERE user_id = :userId AND status = 'SOLVED' AND solved_at >= :from GROUP BY YEARWEEK(solved_at, 1) ORDER BY week", nativeQuery = true)
    List<Object[]> findWeeklyProgress(@Param("userId") Long userId, @Param("from") LocalDate from);

    @Query(value = "SELECT YEARWEEK(solved_at, 1) as week, COUNT(*) as count FROM user_dsa_progress WHERE user_id = :userId AND status = 'SOLVED' GROUP BY YEARWEEK(solved_at, 1) ORDER BY week DESC LIMIT 12", nativeQuery = true)
    List<Object[]> findWeeklyGrowth(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM UserDSAProgress p WHERE p.user.id = :userId AND p.status = 'ATTEMPTED'")
    Long countAttemptedByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM UserDSAProgress p WHERE p.user.id = :userId AND p.status = 'REVISIT'")
    Long countRevisitByUserId(@Param("userId") Long userId);
}
