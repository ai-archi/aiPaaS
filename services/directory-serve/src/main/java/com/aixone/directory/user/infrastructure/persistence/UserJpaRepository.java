package com.aixone.directory.user.infrastructure.persistence;

import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserDbo, UUID> {

    Optional<UserDbo> findByEmail(String email);
    
    Optional<UserDbo> findByTenantIdAndEmail(String tenantId, String email);

    Optional<UserDbo> findByTenantIdAndId(String tenantId, UUID id);

    boolean existsByEmail(String email);
} 