package com.skillbridge.module.auth.service;

import com.skillbridge.common.response.ApiResponse;
import com.skillbridge.module.auth.dto.*;

public interface AuthService {
    ApiResponse<AuthResponse> register(RegisterRequest request);
    ApiResponse<AuthResponse> login(LoginRequest request);
    ApiResponse<AuthResponse> getCurrentUser(Long userId);
    ApiResponse<Void> changePassword(Long userId, ChangePasswordRequest request);
}
