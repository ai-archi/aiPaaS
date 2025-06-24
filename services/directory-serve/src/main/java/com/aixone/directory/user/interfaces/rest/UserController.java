package com.aixone.directory.user.interfaces.rest;

import com.aixone.directory.user.application.UserApplicationService;
import com.aixone.directory.user.application.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    public ResponseEntity<Void> createUser(@PathVariable String tenantId, @RequestBody UserDto.CreateUserCommand command) {
        UserDto newUserDto = userApplicationService.createUser(tenantId, command);
        return ResponseEntity.created(URI.create(String.format("/api/v1/tenants/%s/users/%s", tenantId, newUserDto.getId()))).build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String tenantId, @PathVariable UUID userId) {
        return userApplicationService.getUser(tenantId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateUserProfile(@PathVariable String tenantId, @PathVariable UUID userId, @RequestBody UserDto.UpdateProfileCommand command) {
        userApplicationService.updateUserProfile(tenantId, userId, command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String tenantId, @PathVariable UUID userId, @RequestBody UserDto.ChangePasswordCommand command) {
        userApplicationService.changePassword(tenantId, userId, command);
        return ResponseEntity.ok().build();
    }
}

