package com.aixone.workbench.message.domain.repository;

import com.aixone.workbench.message.domain.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 消息仓储接口
 * 
 * @author AixOne Team
 * @since 1.0.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    Page<Message> findByUserIdAndTenantIdOrderByCreatedAtDesc(UUID userId, UUID tenantId, Pageable pageable);
    
    List<Message> findByUserIdAndTenantIdAndStatus(UUID userId, UUID tenantId, Message.MessageStatus status);
    
    long countByUserIdAndTenantIdAndStatus(UUID userId, UUID tenantId, Message.MessageStatus status);
}

