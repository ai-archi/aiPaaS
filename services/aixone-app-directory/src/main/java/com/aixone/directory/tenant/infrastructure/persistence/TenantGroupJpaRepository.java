package com.aixone.directory.tenant.infrastructure.persistence;

import com.aixone.directory.tenant.infrastructure.persistence.dbo.TenantGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 租户组 JPA 仓储
 */
@Repository
public interface TenantGroupJpaRepository extends JpaRepository<TenantGroupEntity, String> {

    List<TenantGroupEntity> findByParentId(String parentId);

    @Query("SELECT g FROM TenantGroupEntity g WHERE g.parentId IS NULL OR g.parentId = ''")
    List<TenantGroupEntity> findRootGroups();

    boolean existsByName(String name);

    List<TenantGroupEntity> findByStatusOrderBySortOrderAsc(String status);
}

