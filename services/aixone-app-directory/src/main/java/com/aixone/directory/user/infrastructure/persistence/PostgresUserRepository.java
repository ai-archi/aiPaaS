package com.aixone.directory.user.infrastructure.persistence;

import com.aixone.directory.user.domain.aggregate.User;
import com.aixone.directory.user.domain.repository.UserRepository;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository("userPostgresRepository")
public class PostgresUserRepository implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public PostgresUserRepository(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void save(User user) {
        UserDbo dbo = mapper.toDbo(user);
        jpaRepository.save(dbo);
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByTenantIdAndEmail(String tenantId, String email) {
        return jpaRepository.findByTenantIdAndEmail(tenantId, email).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByTenantIdAndId(String tenantId, String userId) {
        return jpaRepository.findByTenantIdAndId(tenantId, userId).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public java.util.List<User> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }
}

