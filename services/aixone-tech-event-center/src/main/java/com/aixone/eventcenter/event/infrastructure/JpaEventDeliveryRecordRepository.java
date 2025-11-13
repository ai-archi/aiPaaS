package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.EventDeliveryRecord;
import com.aixone.eventcenter.event.domain.EventDeliveryRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * JPA事件分发记录仓储实现
 */
@Repository
public interface JpaEventDeliveryRecordRepository extends JpaRepository<EventDeliveryRecord, Long>, EventDeliveryRecordRepository {
    
    @Override
    @Query("SELECT r FROM EventDeliveryRecord r WHERE r.eventId = :eventId AND r.subscriptionId = :subscriptionId")
    Optional<EventDeliveryRecord> findByEventIdAndSubscriptionId(@Param("eventId") Long eventId, @Param("subscriptionId") Long subscriptionId);
    
    @Override
    @Query("SELECT r FROM EventDeliveryRecord r WHERE r.status = 'RETRYING' AND r.nextRetryAt <= :now")
    List<EventDeliveryRecord> findRecordsForRetry(@Param("now") Instant now);
    
    @Override
    @Query("SELECT r FROM EventDeliveryRecord r WHERE r.tenantId = :tenantId")
    List<EventDeliveryRecord> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT r FROM EventDeliveryRecord r WHERE r.eventId = :eventId")
    List<EventDeliveryRecord> findByEventId(@Param("eventId") Long eventId);
    
    @Override
    @Query("SELECT r FROM EventDeliveryRecord r WHERE r.subscriptionId = :subscriptionId")
    List<EventDeliveryRecord> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
}

