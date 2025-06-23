package com.aixone.directory.tenant.application;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

public final class TenantDto {
    private TenantDto() {}

    @Value
    public static class CreateTenantCommand {
        String name;
    }

    @Value
    @Builder
    public static class TenantView {
        UUID id;
        String name;
        String status;
        LocalDateTime createdAt;
    }
} 