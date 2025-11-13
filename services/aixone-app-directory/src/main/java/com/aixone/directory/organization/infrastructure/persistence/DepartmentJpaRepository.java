package com.aixone.directory.organization.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentDbo, String>, JpaSpecificationExecutor<DepartmentDbo> {
    List<DepartmentDbo> findByOrganizationId(String orgId);
    List<DepartmentDbo> findByParentId(String parentId);
    
    @Query("SELECT d FROM DepartmentDbo d JOIN FETCH d.organization WHERE d.tenantId = :tenantId")
    List<DepartmentDbo> findByTenantId(@Param("tenantId") String tenantId);
    
    void deleteByOrganizationId(String orgId);
} 