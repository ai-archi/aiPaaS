package com.aixone.directory.user.interfaces.internal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.user.application.UserApplicationService;
import com.aixone.directory.user.application.UserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final UserApplicationService userApplicationService;

    @GetMapping("/credentials/{email}")
    public ResponseEntity<UserDto.UserCredentialsView> getUserCredentials(@PathVariable String tenantId, @PathVariable String email) {
        return userApplicationService.findUserCredentialsByEmail(tenantId, email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 