package com.skillbridge.module.dsa.repository;

import com.skillbridge.common.enums.Difficulty;
import com.skillbridge.module.dsa.entity.DSAProblem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DSAProblemRepository extends JpaRepository<DSAProblem, Long> {
    Page<DSAProblem> findByIsDeletedFalse(Pageable pageable);
    Page<DSAProblem> findByTopicAndIsDeletedFalse(String topic, Pageable pageable);
    Page<DSAProblem> findByDifficultyAndIsDeletedFalse(Difficulty difficulty, Pageable pageable);
    Page<DSAProblem> findByTopicAndDifficultyAndIsDeletedFalse(String topic, Difficulty difficulty, Pageable pageable);
    List<DSAProblem> findByIsDeletedFalse();
    long countByIsDeletedFalse();

    @Query("SELECT DISTINCT p.topic FROM DSAProblem p WHERE p.isDeleted = false ORDER BY p.topic")
    List<String> findAllTopics();
}
