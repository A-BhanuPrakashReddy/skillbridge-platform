package com.skillbridge.module.aptitude.repository;

import com.skillbridge.common.enums.AptitudeCategory;
import com.skillbridge.module.aptitude.entity.AptitudeQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AptitudeQuestionRepository extends JpaRepository<AptitudeQuestion, Long> {
    Page<AptitudeQuestion> findByCategoryAndIsDeletedFalse(AptitudeCategory category, Pageable pageable);
    long countByCategoryAndIsDeletedFalse(AptitudeCategory category);

    @Query(value = "SELECT * FROM aptitude_questions WHERE category = :category AND is_deleted = 0 ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<AptitudeQuestion> findRandomByCategory(@Param("category") String category, @Param("count") int count);
}
