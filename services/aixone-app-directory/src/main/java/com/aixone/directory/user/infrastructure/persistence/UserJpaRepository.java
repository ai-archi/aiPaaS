package com.aixone.directory.user.infrastructure.persistence;

import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<UserDbo, String>, JpaSpecificationExecutor<UserDbo> {

    Optional<UserDbo> findByEmail(String email);
    
    Optional<UserDbo> findByTenantIdAndEmail(String tenantId, String email);

    Optional<UserDbo> findByTenantIdAndId(String tenantId, String id);

    boolean existsByEmail(String email);
    
    java.util.List<UserDbo> findByTenantId(String tenantId);
} 