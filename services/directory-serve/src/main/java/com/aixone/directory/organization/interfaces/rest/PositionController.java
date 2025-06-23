package com.aixone.directory.organization.interfaces.rest;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.organization.application.OrganizationApplicationService;
import com.aixone.directory.organization.application.dto.CreatePositionRequest;
import com.aixone.directory.organization.application.dto.PositionDto;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/organizations/{organizationId}/positions")
public class PositionController {

    private final OrganizationApplicationService organizationApplicationService;

    public PositionController(OrganizationApplicationService organizationApplicationService) {
        this.organizationApplicationService = organizationApplicationService;
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId,
            @RequestBody CreatePositionRequest request) {

        PositionDto newPosition = organizationApplicationService.addPositionToOrganization(organizationId, request);
        return new ResponseEntity<>(newPosition, HttpStatus.CREATED);
    }
} 