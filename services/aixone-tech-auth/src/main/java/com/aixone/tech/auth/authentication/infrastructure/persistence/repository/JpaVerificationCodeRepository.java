package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import com.aixone.tech.auth.authentication.domain.repository.VerificationCodeRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.VerificationCodeEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.VerificationCodeMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * VerificationCode JPA 仓储实现
 */
@Repository
public class JpaVerificationCodeRepository implements VerificationCodeRepository {

    private final VerificationCodeJpaRepository jpaRepository;
    private final VerificationCodeMapper mapper;

    public JpaVerificationCodeRepository(VerificationCodeJpaRepository jpaRepository, VerificationCodeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public VerificationCode save(VerificationCode verificationCode) {
        VerificationCodeEntity entity = mapper.toEntity(verificationCode);
        VerificationCodeEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<VerificationCode> findByPhoneAndTenantId(String phone, String tenantId) {
        return jpaRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(phone, tenantId, "", LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findByEmailAndTenantId(String email, String tenantId) {
        return jpaRepository.findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(email, tenantId, "", LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findByPhoneAndTenantIdAndCode(String phone, String tenantId, String code) {
        return jpaRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(phone, tenantId, code, LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findByEmailAndTenantIdAndCode(String email, String tenantId, String code) {
        return jpaRepository.findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(email, tenantId, code, LocalDateTime.now())
                .map(mapper::toDomain);
    }

    @Override
    public void deleteExpiredCodes() {
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public void deleteExpiredCodesByTenantId(String tenantId) {
        // 由于 JPA 接口没有按租户删除的方法，这里先实现为删除所有过期的
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public void deleteByCreatedAtBefore(LocalDateTime timestamp) {
        // 由于 JPA 接口没有按创建时间删除的方法，这里先实现为删除所有过期的
        jpaRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }

    @Override
    public boolean isPhoneInCooldown(String phone, String tenantId, int cooldownMinutes) {
        LocalDateTime cooldownTime = LocalDateTime.now().minusMinutes(cooldownMinutes);
        return jpaRepository.findTopByPhoneAndTenantIdOrderByCreatedAtDesc(phone, tenantId)
                .filter(code -> code.getCreatedAt().isAfter(cooldownTime))
                .isPresent();
    }

    @Override
    public boolean isEmailInCooldown(String email, String tenantId, int cooldownMinutes) {
        LocalDateTime cooldownTime = LocalDateTime.now().minusMinutes(cooldownMinutes);
        return jpaRepository.findTopByEmailAndTenantIdOrderByCreatedAtDesc(email, tenantId)
                .filter(code -> code.getCreatedAt().isAfter(cooldownTime))
                .isPresent();
    }

    @Override
    public Optional<VerificationCode> findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
            String phone, String tenantId, String code, LocalDateTime now) {
        return jpaRepository.findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(phone, tenantId, code, now)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
            String email, String tenantId, String code, LocalDateTime now) {
        return jpaRepository.findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(email, tenantId, code, now)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findTopByPhoneAndTenantIdOrderByCreatedAtDesc(String phone, String tenantId) {
        return jpaRepository.findTopByPhoneAndTenantIdOrderByCreatedAtDesc(phone, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<VerificationCode> findTopByEmailAndTenantIdOrderByCreatedAtDesc(String email, String tenantId) {
        return jpaRepository.findTopByEmailAndTenantIdOrderByCreatedAtDesc(email, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public VerificationCode findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(String phone, String tenantId, String type, LocalDateTime expiresAt) {
        return jpaRepository.findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(phone, tenantId, type, expiresAt)
                .map(mapper::toDomain)
                .orElse(null);
    }

    @Override
    public VerificationCode findByEmailAndTenantIdAndTypeAndExpiresAtAfter(String email, String tenantId, String type, LocalDateTime expiresAt) {
        return jpaRepository.findByEmailAndTenantIdAndTypeAndExpiresAtAfter(email, tenantId, type, expiresAt)
                .map(mapper::toDomain)
                .orElse(null);
    }

        @Override
        public void deleteByCodeId(String codeId) {
            jpaRepository.deleteByCodeId(codeId);
        }

        @Override
        public VerificationCode findLatestValidCode(String tenantId, String type, String destination, String purpose, LocalDateTime now) {
            if ("SMS".equalsIgnoreCase(type)) {
                return findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(destination, tenantId, type, now);
            } else if ("EMAIL".equalsIgnoreCase(type)) {
                return findByEmailAndTenantIdAndTypeAndExpiresAtAfter(destination, tenantId, type, now);
            }
            return null;
        }
    }