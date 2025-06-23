package com.aixone.directory.group.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.aixone.directory.group.domain.aggregate.Group;
import com.aixone.directory.group.domain.repository.GroupRepository;
import com.aixone.directory.group.infrastructure.persistence.dbo.GroupDbo;

import lombok.RequiredArgsConstructor;

@Repository("groupPostgresRepository") // Named to avoid conflict if other impls are added
@RequiredArgsConstructor
public class PostgresGroupRepository implements GroupRepository {

    private final GroupJpaRepository jpaRepository;
    private final GroupMapper mapper;

    @Override
    public void save(Group group) {
        GroupDbo dbo = mapper.toDbo(group);
        jpaRepository.save(dbo);
    }

    @Override
    public Optional<Group> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Group> findByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantId(tenantId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
} 