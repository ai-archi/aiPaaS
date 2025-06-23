package com.aixone.directory.user.interfaces.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.user.application.UserApplicationService;
import com.aixone.directory.user.application.UserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    public ResponseEntity<UUID> createUser(@PathVariable String tenantId, @RequestBody UserDto.CreateUserCommand command) {
        UUID userId = userApplicationService.createUser(tenantId, command);
        return ResponseEntity.status(201).body(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.UserPublicView> getUserById(@PathVariable String tenantId, @PathVariable UUID userId) {
        return userApplicationService.findUserById(tenantId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateUserProfile(@PathVariable String tenantId, @PathVariable UUID userId, @RequestBody UserDto.UpdateProfileCommand command) {
        // In a real app, you'd verify that the authenticated user matches userId
        userApplicationService.updateUserProfile(tenantId, userId, command);
        return ResponseEntity.ok().build();
    }
} 