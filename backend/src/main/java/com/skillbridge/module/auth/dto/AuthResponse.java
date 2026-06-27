package com.skillbridge.module.auth.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;
    private String token;
    private long expiresIn;
}
