package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.VerificationCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * VerificationCode JPA Repository
 */
@Repository
public interface VerificationCodeJpaRepository extends JpaRepository<VerificationCodeEntity, Long> {
    
    Optional<VerificationCodeEntity> findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(String phone, String tenantId, String code, LocalDateTime expiresAt);
    
    Optional<VerificationCodeEntity> findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(String email, String tenantId, String code, LocalDateTime expiresAt);
    
    Optional<VerificationCodeEntity> findTopByPhoneAndTenantIdOrderByCreatedAtDesc(String phone, String tenantId);
    
    Optional<VerificationCodeEntity> findTopByEmailAndTenantIdOrderByCreatedAtDesc(String email, String tenantId);
    
    Optional<VerificationCodeEntity> findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(String phone, String tenantId, String type, LocalDateTime expiresAt);
    
    Optional<VerificationCodeEntity> findByEmailAndTenantIdAndTypeAndExpiresAtAfter(String email, String tenantId, String type, LocalDateTime expiresAt);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCodeEntity v WHERE v.id = :id")
    void deleteByCodeId(@Param("id") String codeId);
    
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}
