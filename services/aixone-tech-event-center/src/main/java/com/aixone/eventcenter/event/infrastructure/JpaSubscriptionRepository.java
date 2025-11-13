package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Subscription;
import com.aixone.eventcenter.event.domain.SubscriptionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA订阅仓储实现
 */
@Repository
public interface JpaSubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionRepository {
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId")
    List<Subscription> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionId = :subscriptionId AND s.tenantId = :tenantId")
    Optional<Subscription> findBySubscriptionIdAndTenantId(@Param("subscriptionId") Long subscriptionId, 
                                                           @Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.eventType = :eventType")
    List<Subscription> findByEventType(@Param("eventType") String eventType);
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND s.eventType = :eventType")
    List<Subscription> findByTenantIdAndEventType(@Param("tenantId") String tenantId, 
                                                   @Param("eventType") String eventType);
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.tenantId = :tenantId AND s.status = :status")
    List<Subscription> findByTenantIdAndStatus(@Param("tenantId") String tenantId, 
                                               @Param("status") Subscription.SubscriptionStatus status);
    
    @Override
    @Query("SELECT s FROM Subscription s WHERE s.eventType = :eventType AND s.status = :status")
    List<Subscription> findByEventTypeAndStatus(@Param("eventType") String eventType, 
                                                 @Param("status") Subscription.SubscriptionStatus status);
    
    @Override
    @Query("SELECT COUNT(s) FROM Subscription s WHERE s.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
}

