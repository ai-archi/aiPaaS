package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.User;
import com.aixone.tech.auth.authentication.domain.repository.UserRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.UserEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * User JPA 仓储实现
 */
@Repository
public class JpaUserRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public JpaUserRepository(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findByUsernameAndTenantId(String username, String tenantId) {
        return jpaRepository.findByUsernameAndTenantId(username, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByUsernameAndTenantId(String username, String tenantId) {
        return jpaRepository.existsByUsernameAndTenantId(username, tenantId);
    }
}
