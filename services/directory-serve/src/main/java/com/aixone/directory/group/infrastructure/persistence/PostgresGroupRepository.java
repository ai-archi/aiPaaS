package com.aixone.directory.group.infrastructure.persistence;

import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.domain.repository.GroupRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PostgresGroupRepository implements GroupRepository {

    private final GroupJpaRepository jpaRepository;
    private final GroupMapper mapper;

    public PostgresGroupRepository(GroupJpaRepository jpaRepository, GroupMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Group group) {
        // Temporarily disabled
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Group> findByTenantId(UUID tenantId) {
        return Collections.emptyList();
    }
}

