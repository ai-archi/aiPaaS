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
import com.aixone.directory.organization.application.dto.CreateDepartmentRequest;
import com.aixone.directory.organization.application.dto.DepartmentDto;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/organizations/{organizationId}/departments")
public class DepartmentController {

    private final OrganizationApplicationService organizationApplicationService;

    public DepartmentController(OrganizationApplicationService organizationApplicationService) {
        this.organizationApplicationService = organizationApplicationService;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(
            @PathVariable UUID tenantId,
            @PathVariable UUID organizationId,
            @RequestBody CreateDepartmentRequest request) {
        
        DepartmentDto newDepartment = organizationApplicationService.addDepartmentToOrganization(organizationId, request);
        return new ResponseEntity<>(newDepartment, HttpStatus.CREATED);
    }
} 