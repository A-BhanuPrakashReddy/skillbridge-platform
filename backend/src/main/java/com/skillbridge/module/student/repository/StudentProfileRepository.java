package com.skillbridge.module.student.repository;

import com.skillbridge.module.student.entity.StudentProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long>, JpaSpecificationExecutor<StudentProfile> {
    Optional<StudentProfile> findByUserId(Long userId);
    Optional<StudentProfile> findByUserIdAndIsDeletedFalse(Long userId);
    List<StudentProfile> findByBranchAndIsDeletedFalse(String branch);

    @Query("SELECT s FROM StudentProfile s WHERE s.isDeleted = false AND s.skills LIKE %:skill%")
    List<StudentProfile> findBySkillContaining(@Param("skill") String skill);

    @Query("SELECT COUNT(s) FROM StudentProfile s WHERE s.isDeleted = false")
    Long countActive();
}
