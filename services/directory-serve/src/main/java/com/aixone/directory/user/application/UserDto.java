package com.aixone.directory.user.application;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

public final class UserDto {
    private UserDto() {}

    @Value
    @Builder
    public static class CreateUserCommand {
        String email;
        String password;
        String username;
    }

    @Value
    @Builder
    public static class UpdateProfileCommand {
        UUID userId;
        String username;
        String avatarUrl;
        String bio;
    }

    @Value
    @Builder
    public static class UserPublicView {
        UUID id;
        String username;
        String avatarUrl;
        LocalDateTime createdAt;
    }
    
    @Value
    @Builder
    public static class UserCredentialsView {
        UUID id;
        String hashedPassword;
        boolean isActive;
    }
} 