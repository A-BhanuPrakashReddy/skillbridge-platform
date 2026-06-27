package com.skillbridge.module.auth.service;

import com.skillbridge.common.enums.Role;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.dto.*;
import com.skillbridge.module.auth.entity.User;
import com.skillbridge.module.auth.repository.UserRepository;
import com.skillbridge.module.student.entity.StudentProfile;
import com.skillbridge.module.student.repository.StudentProfileRepository;
import com.skillbridge.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .isDeleted(false)
                .build();
        userRepository.save(user);

        if (request.getRole() == Role.STUDENT) {
            StudentProfile profile = StudentProfile.builder()
                    .user(user)
                    .readinessScore(BigDecimal.ZERO)
                    .activeBacklogs(0)
                    .totalBacklogs(0)
                    .isDeleted(false)
                    .build();
            studentProfileRepository.save(profile);
        }
        return ApiResponse.success("Registration successful. Please login.");
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = (User) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(authentication);
        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .expiresIn(jwtTokenProvider.getExpiration())
                .build();
        return ApiResponse.success("Login successful", response);
    }

    @Override
    public ApiResponse<AuthResponse> getCurrentUser(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        AuthResponse response = AuthResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
        return ApiResponse.success("User fetched", response);
    }

    @Override
    @Transactional
    public ApiResponse<Void> changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return ApiResponse.success("Password changed successfully");
    }
}
