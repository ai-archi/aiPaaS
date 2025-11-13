package com.aixone.directory.user.interfaces.rest;

import com.aixone.directory.user.application.UserApplicationService;
import com.aixone.directory.user.application.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.Set;

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

    @GetMapping
    public ResponseEntity<java.util.List<UserDto>> getUsers(@PathVariable String tenantId) {
        return ResponseEntity.ok(userApplicationService.getUsers(tenantId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable String tenantId, @PathVariable String userId) {
        return userApplicationService.getUser(tenantId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}/profile")
    public ResponseEntity<Void> updateUserProfile(@PathVariable String tenantId, @PathVariable String userId, @RequestBody UserDto.UpdateProfileCommand command) {
        userApplicationService.updateUserProfile(tenantId, userId, command);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(@PathVariable String tenantId, @PathVariable String userId, @RequestBody UserDto.ChangePasswordCommand command) {
        userApplicationService.changePassword(tenantId, userId, command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/departments")
    public void assignDepartmentsToUser(@PathVariable String userId, @RequestBody Set<String> departmentIds) {
        userApplicationService.assignDepartmentsToUser(userId, departmentIds);
    }

    @DeleteMapping("/{userId}/departments")
    public void removeDepartmentsFromUser(@PathVariable String userId, @RequestBody Set<String> departmentIds) {
        userApplicationService.removeDepartmentsFromUser(userId, departmentIds);
    }

    @PostMapping("/{userId}/positions")
    public void assignPositionsToUser(@PathVariable String userId, @RequestBody Set<String> positionIds) {
        userApplicationService.assignPositionsToUser(userId, positionIds);
    }

    @DeleteMapping("/{userId}/positions")
    public void removePositionsFromUser(@PathVariable String userId, @RequestBody Set<String> positionIds) {
        userApplicationService.removePositionsFromUser(userId, positionIds);
    }

    @PostMapping("/{userId}/roles")
    public void assignRolesToUser(@PathVariable String userId, @RequestBody Set<String> roleIds) {
        userApplicationService.assignRolesToUser(userId, roleIds);
    }

    @DeleteMapping("/{userId}/roles")
    public void removeRolesFromUser(@PathVariable String userId, @RequestBody Set<String> roleIds) {
        userApplicationService.removeRolesFromUser(userId, roleIds);
    }
}

