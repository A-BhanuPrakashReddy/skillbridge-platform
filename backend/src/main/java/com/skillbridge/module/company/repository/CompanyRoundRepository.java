package com.skillbridge.module.company.repository;

import com.skillbridge.module.company.entity.CompanyRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CompanyRoundRepository extends JpaRepository<CompanyRound, Long> {
    List<CompanyRound> findByCompanyIdAndIsDeletedFalseOrderByRoundNumberAsc(Long companyId);
}
