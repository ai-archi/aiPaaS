package com.aixone.directory.user.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.domain.repository.UserRepository;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostgresUserRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    public void save(User user) {
        UserDbo dbo = userMapper.toDbo(user);
        jpaRepository.save(dbo);
    }

    @Override
    public Optional<User> findByTenantIdAndId(String tenantId, UUID id) {
        return jpaRepository.findByTenantIdAndId(tenantId, id)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByTenantIdAndEmail(String tenantId, String email) {
        return jpaRepository.findByTenantIdAndEmail(tenantId, email)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
} 