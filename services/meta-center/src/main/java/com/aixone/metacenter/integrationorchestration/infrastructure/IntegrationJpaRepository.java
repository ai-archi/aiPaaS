package com.aixone.metacenter.integrationorchestration.infrastructure;

import com.aixone.metacenter.integrationorchestration.domain.Integration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IntegrationJpaRepository extends JpaRepository<Integration, Long>, JpaSpecificationExecutor<Integration> {
    List<Integration> findByTenantId(String tenantId);
    List<Integration> findByStatus(String status);
    List<Integration> findByIntegrationType(String integrationType);
    Optional<Integration> findByName(String name);
    Optional<Integration> findByTenantIdAndName(String tenantId, String name);
    List<Integration> findBySourceSystem(String sourceSystem);
    List<Integration> findByTargetSystem(String targetSystem);
    List<Integration> findByTenantIdAndStatus(String tenantId, String status);
}
