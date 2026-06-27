package com.skillbridge.module.aptitude.repository;

import com.skillbridge.module.aptitude.entity.QuizAttemptDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizAttemptDetailRepository extends JpaRepository<QuizAttemptDetail, Long> {
    List<QuizAttemptDetail> findByAttemptId(Long attemptId);
}
