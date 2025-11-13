package com.aixone.directory.group.application.dto;

import lombok.Data;
import java.util.Set;

@Data
public class ReplaceMembersRequest {
    private Set<String> userIds;
}

