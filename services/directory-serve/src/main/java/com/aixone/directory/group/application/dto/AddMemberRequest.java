package com.aixone.directory.group.application.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AddMemberRequest {
    private UUID userId;
} 