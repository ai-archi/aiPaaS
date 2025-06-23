package com.aixone.directory.user.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Profile is a Value Object, representing user's public-facing information.
 * It is immutable.
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Profile {
    private final String username;
    private final String avatarUrl;
    private final String bio;
} 