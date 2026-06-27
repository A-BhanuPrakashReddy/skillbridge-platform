package com.skillbridge.module.interview.repository;

import com.skillbridge.module.interview.entity.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {

    @Query("SELECT COUNT(m) FROM MockInterview m WHERE m.student.user.id = :userId AND m.status = 'PENDING'")
    Long countPendingByStudent(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM MockInterview m WHERE m.student.user.id = :userId AND m.status = 'COMPLETED'")
    Long countCompletedByStudent(@Param("userId") Long userId);

    @Query("SELECT AVG(m.score) FROM MockInterview m WHERE m.student.user.id = :userId AND m.status = 'COMPLETED'")
    Double avgScoreByStudent(@Param("userId") Long userId);

    @Query("SELECT m FROM MockInterview m WHERE m.student.user.id = :userId ORDER BY m.scheduledAt DESC")
    List<MockInterview> findByStudentUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM MockInterview m WHERE m.officer.id = :officerId ORDER BY m.scheduledAt ASC")
    List<MockInterview> findByOfficerId(@Param("officerId") Long officerId);
}
