package com.aixone.directory.group.application.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ReplaceRolesRequest {
    private Set<String> roleIds;
}

