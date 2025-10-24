package com.aixone.tech.auth.authentication.domain.repository;

import com.aixone.tech.auth.authentication.domain.model.VerificationCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 验证码仓储接口
 */
public interface VerificationCodeRepository {

    /**
     * 保存验证码
     */
    VerificationCode save(VerificationCode verificationCode);

    /**
     * 根据手机号和租户ID查找验证码
     */
    Optional<VerificationCode> findByPhoneAndTenantId(String phone, String tenantId);

    /**
     * 根据邮箱和租户ID查找验证码
     */
    Optional<VerificationCode> findByEmailAndTenantId(String email, String tenantId);

    /**
     * 根据手机号、租户ID和验证码查找
     */
    Optional<VerificationCode> findByPhoneAndTenantIdAndCode(String phone, String tenantId, String code);

    /**
     * 根据邮箱、租户ID和验证码查找
     */
    Optional<VerificationCode> findByEmailAndTenantIdAndCode(String email, String tenantId, String code);

    /**
     * 删除过期的验证码
     */
    void deleteExpiredCodes();

    /**
     * 删除指定租户的过期验证码
     */
    void deleteExpiredCodesByTenantId(String tenantId);

    /**
     * 删除指定时间之前的验证码
     */
    void deleteByCreatedAtBefore(LocalDateTime timestamp);

    /**
     * 检查手机号是否在冷却期
     */
    boolean isPhoneInCooldown(String phone, String tenantId, int cooldownMinutes);

    /**
     * 检查邮箱是否在冷却期
     */
    boolean isEmailInCooldown(String email, String tenantId, int cooldownMinutes);

    /**
     * 根据手机号、租户ID、验证码查找未使用的有效验证码
     */
    Optional<VerificationCode> findByPhoneAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
            String phone, String tenantId, String code, LocalDateTime now);

    /**
     * 根据邮箱、租户ID、验证码查找未使用的有效验证码
     */
    Optional<VerificationCode> findByEmailAndTenantIdAndCodeAndUsedFalseAndExpiresAtAfter(
            String email, String tenantId, String code, LocalDateTime now);

    /**
     * 查找手机号最新的验证码
     */
    Optional<VerificationCode> findTopByPhoneAndTenantIdOrderByCreatedAtDesc(String phone, String tenantId);

    /**
     * 查找邮箱最新的验证码
     */
    Optional<VerificationCode> findTopByEmailAndTenantIdOrderByCreatedAtDesc(String email, String tenantId);

    /**
     * 根据手机号、租户ID、类型和过期时间查找验证码
     */
    VerificationCode findByPhoneAndTenantIdAndTypeAndExpiresAtAfter(String phone, String tenantId, String type, LocalDateTime expiresAt);

    /**
     * 根据邮箱、租户ID、类型和过期时间查找验证码
     */
    VerificationCode findByEmailAndTenantIdAndTypeAndExpiresAtAfter(String email, String tenantId, String type, LocalDateTime expiresAt);

    /**
     * 根据验证码ID删除验证码
     */
    void deleteByCodeId(String codeId);

    /**
     * 查找最新的有效验证码
     */
    VerificationCode findLatestValidCode(String tenantId, String type, String destination, String purpose, LocalDateTime now);
}
