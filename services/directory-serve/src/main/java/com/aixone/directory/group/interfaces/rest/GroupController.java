package com.aixone.directory.group.interfaces.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.group.application.GroupApplicationService;
import com.aixone.directory.group.application.dto.AddMemberRequest;
import com.aixone.directory.group.application.dto.CreateGroupRequest;
import com.aixone.directory.group.application.dto.GroupDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupApplicationService groupApplicationService;

    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@PathVariable UUID tenantId, @RequestBody CreateGroupRequest request) {
        GroupDto newGroup = groupApplicationService.createGroup(tenantId, request);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable UUID tenantId, @PathVariable UUID groupId) {
        GroupDto group = groupApplicationService.getGroup(tenantId, groupId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID groupId,
            @RequestBody AddMemberRequest request) {
        groupApplicationService.addMemberToGroup(tenantId, groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID groupId,
            @PathVariable UUID userId) {
        groupApplicationService.removeMemberFromGroup(tenantId, groupId, userId);
        return ResponseEntity.noContent().build();
    }
} 