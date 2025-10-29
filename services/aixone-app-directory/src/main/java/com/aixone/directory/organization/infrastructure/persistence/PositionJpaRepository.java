package com.aixone.directory.organization.infrastructure.persistence;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aixone.directory.organization.infrastructure.persistence.dbo.PositionDbo;

@Repository
public interface PositionJpaRepository extends JpaRepository<PositionDbo, String> {
    List<PositionDbo> findByOrganizationId(String orgId);
    void deleteByOrganizationId(String orgId);
} 