package com.aixone.directory.organization.interfaces.rest;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aixone.directory.organization.application.OrganizationApplicationService;
import com.aixone.directory.organization.application.dto.CreateDepartmentRequest;
import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.organization.application.DepartmentApplicationService;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/organizations/{organizationId}/departments")
public class DepartmentController {

    private final OrganizationApplicationService organizationApplicationService;
    private final DepartmentApplicationService departmentApplicationService;

    public DepartmentController(OrganizationApplicationService organizationApplicationService, DepartmentApplicationService departmentApplicationService) {
        this.organizationApplicationService = organizationApplicationService;
        this.departmentApplicationService = departmentApplicationService;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> createDepartment(
            @PathVariable String tenantId,
            @PathVariable String organizationId,
            @RequestBody CreateDepartmentRequest request) {
        
        DepartmentDto newDepartment = organizationApplicationService.addDepartmentToOrganization(organizationId, request);
        return new ResponseEntity<>(newDepartment, HttpStatus.CREATED);
    }

    @PostMapping("/{departmentId}/users")
    public void assignUsersToDepartment(@PathVariable String departmentId, @RequestBody Set<String> userIds) {
        departmentApplicationService.assignUsersToDepartment(departmentId, userIds);
    }

    @DeleteMapping("/{departmentId}/users")
    public void removeUsersFromDepartment(@PathVariable String departmentId, @RequestBody Set<String> userIds) {
        departmentApplicationService.removeUsersFromDepartment(departmentId, userIds);
    }
} 