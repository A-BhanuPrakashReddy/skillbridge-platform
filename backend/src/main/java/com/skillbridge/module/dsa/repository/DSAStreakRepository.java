package com.skillbridge.module.dsa.repository;

import com.skillbridge.module.dsa.entity.DSAStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DSAStreakRepository extends JpaRepository<DSAStreak, Long> {
    Optional<DSAStreak> findByUserId(Long userId);
}
