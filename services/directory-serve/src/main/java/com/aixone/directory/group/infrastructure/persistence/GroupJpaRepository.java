package com.aixone.directory.group.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;

@Repository
public interface GroupJpaRepository extends JpaRepository<GroupDbo, String> {
    List<GroupDbo> findByTenantId(String tenantId);
} 