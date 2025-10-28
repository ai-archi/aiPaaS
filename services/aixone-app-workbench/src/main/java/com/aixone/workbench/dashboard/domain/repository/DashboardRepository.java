package com.aixone.workbench.dashboard.domain.repository;

import com.aixone.workbench.dashboard.domain.model.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 仪表盘仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, UUID> {
    
    List<Dashboard> findByUserId(UUID userId);
    
    List<Dashboard> findByUserIdAndTenantId(UUID userId, UUID tenantId);
    
    Optional<Dashboard> findByIdAndUserIdAndTenantId(UUID id, UUID userId, UUID tenantId);
}

