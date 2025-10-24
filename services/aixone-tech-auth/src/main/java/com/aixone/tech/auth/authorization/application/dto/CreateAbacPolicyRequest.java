package com.aixone.tech.auth.authorization.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAbacPolicyRequest {
    @NotBlank
    private String tenantId;
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
    private String description;
    @NotBlank
    @Size(min = 2, max = 100)
    private String resource;
    @NotBlank
    @Size(min = 2, max = 50)
    private String action;
    @NotBlank
    private String condition;
    private Map<String, Object> attributes;
}
