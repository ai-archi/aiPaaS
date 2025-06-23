package com.aixone.directory.organization.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentDbo, UUID> {
    List<DepartmentDbo> findByOrgId(UUID orgId);
    List<DepartmentDbo> findByParentId(UUID parentId);
    void deleteByOrgId(UUID orgId);
} 