package com.aixone.directory.tenant.application;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

public final class TenantDto {
    private TenantDto() {}

    @Value
    @Builder
    public static class CreateTenantCommand {
        String name;
        String groupId;
    }

    @Value
    @Builder
    public static class UpdateTenantCommand {
        String name;
        String groupId;
        String status;
    }

    @Value
    @Builder
    public static class TenantView {
        String id;
        String name;
        String groupId;
        String groupName;
        String status;
        LocalDateTime createdAt;
        LocalDateTime updatedAt;
    }
} 