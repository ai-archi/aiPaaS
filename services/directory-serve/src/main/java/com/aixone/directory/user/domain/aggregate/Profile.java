package com.aixone.directory.user.domain.aggregate;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Profile is a Value Object, representing user's public-facing information.
 * It is immutable.
 */
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Profile {
    private String username;
    private String avatarUrl;
    private String bio;

    public Profile updateWith(Profile other) {
        if (other == null) {
            return this;
        }
        return this.toBuilder()
                .username(other.getUsername() != null ? other.getUsername() : this.getUsername())
                .avatarUrl(other.getAvatarUrl() != null ? other.getAvatarUrl() : this.getAvatarUrl())
                .bio(other.getBio() != null ? other.getBio() : this.getBio())
                .build();
    }
} 