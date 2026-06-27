package com.skillbridge.module.company.repository;

import com.skillbridge.module.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Page<Company> findByIsDeletedFalseAndIsActiveTrue(Pageable pageable);
    Optional<Company> findByIdAndIsDeletedFalse(Long id);
    boolean existsByNameIgnoreCase(String name);
    Page<Company> findByNameContainingIgnoreCaseAndIsDeletedFalse(String name, Pageable pageable);
    Page<Company> findByIndustryAndIsDeletedFalse(String industry, Pageable pageable);
    List<Company> findByIsDeletedFalseAndIsActiveTrue();
    long countByIsDeletedFalseAndIsActiveTrue();

    @Query("SELECT c FROM Company c WHERE c.isDeleted = false AND c.isActive = true AND c.minCgpa <= :cgpa AND c.maxBacklogs >= :backlogs")
    List<Company> findEligibleCompanies(@Param("cgpa") BigDecimal cgpa, @Param("backlogs") Integer backlogs);
}
