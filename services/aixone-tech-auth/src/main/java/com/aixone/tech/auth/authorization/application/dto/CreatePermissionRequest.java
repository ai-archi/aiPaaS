package com.aixone.tech.auth.authorization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePermissionRequest {
    @NotBlank
    private String tenantId;
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
    @NotBlank
    @Size(min = 2, max = 100)
    private String resource;
    @NotBlank
    @Size(min = 2, max = 50)
    private String action;
    private String description;
}
