package com.aixone.directory.group.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.aixone.directory.group.domain.aggregate.Group;

public interface GroupRepository {
    void save(Group group);
    Optional<Group> findById(String id);
    List<Group> findByTenantId(String tenantId);
    void deleteById(String id);
} 