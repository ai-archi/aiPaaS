package com.aixone.directory.role.application.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AddMemberToRoleRequest {
    private UUID userId;
} 