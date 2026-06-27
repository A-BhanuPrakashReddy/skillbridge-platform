package com.skillbridge.module.officer.service;

import com.skillbridge.module.officer.dto.StudentFilterRequest;
import com.skillbridge.module.student.entity.StudentProfile;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StudentSpecification {

    public static Specification<StudentProfile> withFilters(StudentFilterRequest filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("isDeleted"), false));
            if (StringUtils.hasText(filter.getDepartment())) {
                predicates.add(cb.equal(root.get("branch"), filter.getDepartment()));
            }
            if (filter.getMinCgpa() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("cgpa"), BigDecimal.valueOf(filter.getMinCgpa())));
            }
            if (filter.getMaxCgpa() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("cgpa"), BigDecimal.valueOf(filter.getMaxCgpa())));
            }
            if (filter.getMaxBacklogs() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("activeBacklogs"), filter.getMaxBacklogs()));
            }
            if (filter.getMinReadinessScore() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("readinessScore"), BigDecimal.valueOf(filter.getMinReadinessScore())));
            }
            if (StringUtils.hasText(filter.getSkill())) {
                predicates.add(cb.like(root.get("skills"), "%" + filter.getSkill() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
