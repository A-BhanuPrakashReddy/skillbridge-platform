package com.skillbridge.module.auth.repository;

import com.skillbridge.common.enums.Role;
import com.skillbridge.module.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    Optional<User> findByIdAndIsDeletedFalse(Long id);
    boolean existsByEmail(String email);
    List<User> findByRoleAndIsDeletedFalse(Role role);
}
