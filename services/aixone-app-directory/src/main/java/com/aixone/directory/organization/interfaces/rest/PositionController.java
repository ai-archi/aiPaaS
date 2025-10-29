package com.aixone.directory.organization.interfaces.rest;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.organization.application.OrganizationApplicationService;
import com.aixone.directory.organization.application.PositionApplicationService;
import com.aixone.directory.organization.application.dto.CreatePositionRequest;
import com.aixone.directory.organization.application.dto.PositionDto;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/organizations/{organizationId}/positions")
public class PositionController {

    private final OrganizationApplicationService organizationApplicationService;
    private final PositionApplicationService positionApplicationService;

    public PositionController(OrganizationApplicationService organizationApplicationService, PositionApplicationService positionApplicationService) {
        this.organizationApplicationService = organizationApplicationService;
        this.positionApplicationService = positionApplicationService;
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(
            @PathVariable String tenantId,
            @PathVariable String organizationId,
            @RequestBody CreatePositionRequest request) {

        PositionDto newPosition = organizationApplicationService.addPositionToOrganization(organizationId, request);
        return new ResponseEntity<>(newPosition, HttpStatus.CREATED);
    }

    @PostMapping("/{positionId}/users")
    public void assignUsersToPosition(@PathVariable String positionId, @RequestBody Set<String> userIds) {
        positionApplicationService.assignUsersToPosition(positionId, userIds);
    }

    @DeleteMapping("/{positionId}/users")
    public void removeUsersFromPosition(@PathVariable String positionId, @RequestBody Set<String> userIds) {
        positionApplicationService.removeUsersFromPosition(positionId, userIds);
    }
} 