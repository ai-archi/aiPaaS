package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Event;
import com.aixone.eventcenter.event.domain.EventRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * JPA事件仓储实现
 */
@Repository
public interface JpaEventRepository extends JpaRepository<Event, Long>, EventRepository {
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId")
    List<Event> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.eventType = :eventType")
    List<Event> findByEventType(@Param("eventType") String eventType);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND e.eventType = :eventType")
    List<Event> findByTenantIdAndEventType(@Param("tenantId") String tenantId, @Param("eventType") String eventType);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.timestamp BETWEEN :startTime AND :endTime")
    List<Event> findByTimestampBetween(@Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND e.timestamp BETWEEN :startTime AND :endTime")
    List<Event> findByTenantIdAndTimestampBetween(@Param("tenantId") String tenantId, 
                                                  @Param("startTime") Instant startTime, 
                                                  @Param("endTime") Instant endTime);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.correlationId = :correlationId")
    List<Event> findByCorrelationId(@Param("correlationId") String correlationId);
    
    @Override
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND e.correlationId = :correlationId")
    List<Event> findByTenantIdAndCorrelationId(@Param("tenantId") String tenantId, @Param("correlationId") String correlationId);
    
    @Override
    @Query("SELECT COUNT(e) FROM Event e WHERE e.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventType = :eventType")
    long countByEventType(@Param("eventType") String eventType);
}
