package com.aixone.directory.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;

@Repository
public interface UserJpaRepository extends JpaRepository<UserDbo, UUID> {
    Optional<UserDbo> findByTenantIdAndEmail(String tenantId, String email);
    Optional<UserDbo> findByTenantIdAndId(String tenantId, UUID id);
    boolean existsByEmail(String email);
} 