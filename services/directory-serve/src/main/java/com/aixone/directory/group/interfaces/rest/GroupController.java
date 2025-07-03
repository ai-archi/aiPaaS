package com.aixone.directory.group.interfaces.rest;

import java.util.Set;

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
    public ResponseEntity<GroupDto> createGroup(@PathVariable String tenantId, @RequestBody CreateGroupRequest request) {
        GroupDto newGroup = groupApplicationService.createGroup(tenantId, request);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupDto> getGroup(@PathVariable String tenantId, @PathVariable String groupId) {
        GroupDto group = groupApplicationService.getGroup(tenantId, groupId);
        return ResponseEntity.ok(group);
    }

    @PostMapping("/{groupId}/members")
    public void assignUsersToGroup(@PathVariable String groupId, @RequestBody Set<String> userIds) {
        groupApplicationService.assignUsersToGroup(groupId, userIds);
    }

    @DeleteMapping("/{groupId}/members")
    public void removeUsersFromGroup(@PathVariable String groupId, @RequestBody Set<String> userIds) {
        groupApplicationService.removeUsersFromGroup(groupId, userIds);
    }

    @PostMapping("/{groupId}/roles")
    public void assignRolesToGroup(@PathVariable String groupId, @RequestBody Set<String> roleIds) {
        groupApplicationService.assignRolesToGroup(groupId, roleIds);
    }

    @DeleteMapping("/{groupId}/roles")
    public void removeRolesFromGroup(@PathVariable String groupId, @RequestBody Set<String> roleIds) {
        groupApplicationService.removeRolesFromGroup(groupId, roleIds);
    }
} 