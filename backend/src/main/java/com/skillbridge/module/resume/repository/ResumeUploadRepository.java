package com.skillbridge.module.resume.repository;

import com.skillbridge.module.resume.entity.ResumeUpload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeUploadRepository extends JpaRepository<ResumeUpload, Long> {
    List<ResumeUpload> findByUserIdOrderByUploadedAtDesc(Long userId);
    Optional<ResumeUpload> findByUserIdAndIsLatestTrue(Long userId);
    long countByUserId(Long userId);

    // For OfficerServiceImpl - counts by user id (since ResumeUpload links to User, not StudentProfile directly)
    @Query("SELECT COUNT(r) FROM ResumeUpload r WHERE r.user.id = :userId")
    long countByStudentProfileId(@Param("userId") Long userId);

    @Query("SELECT r.atsScore FROM ResumeUpload r WHERE r.user.id = :userId AND r.isLatest = true")
    Double findLatestAtsByStudentProfileId(@Param("userId") Long userId);

    @Query("SELECT r.uploadedAt, r.atsScore FROM ResumeUpload r WHERE r.user.id = :userId ORDER BY r.uploadedAt ASC")
    List<Object[]> findScoreHistoryByStudentId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ResumeUpload r SET r.isLatest = false WHERE r.user.id = :userId")
    void markAllNotLatestForUser(@Param("userId") Long userId);
}
