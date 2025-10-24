package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import com.aixone.tech.auth.authorization.infrastructure.persistence.entity.UserRoleEntity;
import com.aixone.tech.auth.authorization.infrastructure.persistence.mapper.UserRoleMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * UserRole JPA 仓储实现
 */
@Repository
public class JpaUserRoleRepository implements UserRoleRepository {

    private final UserRoleJpaRepository jpaRepository;
    private final UserRoleMapper mapper;

    public JpaUserRoleRepository(UserRoleJpaRepository jpaRepository, UserRoleMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<UserRole> findByTenantIdAndUserId(String tenantId, String userId) {
        return jpaRepository.findByTenantIdAndUserId(tenantId, userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public UserRole save(UserRole userRole) {
        UserRoleEntity entity = mapper.toEntity(userRole);
        UserRoleEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<UserRole> findByTenantIdAndRoleId(String tenantId, String roleId) {
        return jpaRepository.findByTenantIdAndRoleId(tenantId, roleId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByTenantIdAndUserId(String tenantId, String userId) {
        jpaRepository.deleteByTenantIdAndUserId(tenantId, userId);
    }

    @Override
    public void deleteByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId) {
        jpaRepository.deleteByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }

    @Override
    public boolean existsByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId) {
        return jpaRepository.existsByTenantIdAndUserIdAndRoleId(tenantId, userId, roleId);
    }
}
