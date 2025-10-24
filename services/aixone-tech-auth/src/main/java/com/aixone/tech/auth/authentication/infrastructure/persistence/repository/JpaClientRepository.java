package com.aixone.tech.auth.authentication.infrastructure.persistence.repository;

import com.aixone.tech.auth.authentication.domain.model.Client;
import com.aixone.tech.auth.authentication.domain.repository.ClientRepository;
import com.aixone.tech.auth.authentication.infrastructure.persistence.entity.ClientEntity;
import com.aixone.tech.auth.authentication.infrastructure.persistence.mapper.ClientMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Client JPA 仓储实现
 */
@Repository
public class JpaClientRepository implements ClientRepository {

    private final ClientJpaRepository jpaRepository;
    private final ClientMapper mapper;

    public JpaClientRepository(ClientJpaRepository jpaRepository, ClientMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Client> findByClientIdAndTenantId(String clientId, String tenantId) {
        return jpaRepository.findByClientIdAndTenantId(clientId, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Client> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantId(tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Client save(Client client) {
        ClientEntity entity = mapper.toEntity(client);
        ClientEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Client update(Client client) {
        return save(client);
    }

    @Override
    public void delete(String clientId, String tenantId) {
        jpaRepository.deleteByClientIdAndTenantId(clientId, tenantId);
    }

    @Override
    public boolean existsByClientIdAndTenantId(String clientId, String tenantId) {
        return jpaRepository.existsByClientIdAndTenantId(clientId, tenantId);
    }

    @Override
    public Optional<Client> findByClientId(String clientId) {
        return jpaRepository.findByClientId(clientId)
                .map(mapper::toDomain);
    }
}
