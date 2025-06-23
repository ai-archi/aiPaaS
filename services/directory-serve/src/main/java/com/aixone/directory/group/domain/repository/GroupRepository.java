package com.aixone.directory.group.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.aixone.directory.group.domain.aggregate.Group;

public interface GroupRepository {
    void save(Group group);
    Optional<Group> findById(UUID id);
    List<Group> findByTenantId(UUID tenantId);
} 