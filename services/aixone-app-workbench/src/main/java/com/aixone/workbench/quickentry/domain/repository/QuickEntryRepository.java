package com.aixone.workbench.quickentry.domain.repository;

import com.aixone.workbench.quickentry.domain.model.QuickEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 快捷入口仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface QuickEntryRepository extends JpaRepository<QuickEntry, UUID> {
    
    List<QuickEntry> findByUserId(UUID userId);
    
    List<QuickEntry> findByUserIdAndTenantIdOrderByDisplayOrderAsc(UUID userId, UUID tenantId);
    
    void deleteByUserIdAndTenantId(UUID userId, UUID tenantId);
}

