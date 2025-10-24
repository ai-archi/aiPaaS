package com.aixone.eventcenter.event.infrastructure;

import com.aixone.eventcenter.event.domain.Topic;
import com.aixone.eventcenter.event.domain.TopicRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Topic仓储实现
 */
@Repository
public interface JpaTopicRepository extends JpaRepository<Topic, Long>, TopicRepository {
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.name = :name")
    Optional<Topic> findByName(@Param("name") String name);
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.tenantId = :tenantId")
    List<Topic> findByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.status = :status")
    List<Topic> findByStatus(@Param("status") Topic.TopicStatus status);
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.tenantId = :tenantId AND t.status = :status")
    List<Topic> findByTenantIdAndStatus(@Param("tenantId") String tenantId, @Param("status") Topic.TopicStatus status);
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.owner = :owner")
    List<Topic> findByOwner(@Param("owner") String owner);
    
    @Override
    @Query("SELECT t FROM Topic t WHERE t.tenantId = :tenantId AND t.owner = :owner")
    List<Topic> findByTenantIdAndOwner(@Param("tenantId") String tenantId, @Param("owner") String owner);
    
    @Override
    @Query("SELECT COUNT(t) > 0 FROM Topic t WHERE t.name = :name")
    boolean existsByName(@Param("name") String name);
    
    @Override
    @Query("DELETE FROM Topic t WHERE t.name = :name")
    void deleteByName(@Param("name") String name);
    
    @Override
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.tenantId = :tenantId")
    long countByTenantId(@Param("tenantId") String tenantId);
    
    @Override
    @Query("SELECT COUNT(t) FROM Topic t WHERE t.status = :status")
    long countByStatus(@Param("status") Topic.TopicStatus status);
}
