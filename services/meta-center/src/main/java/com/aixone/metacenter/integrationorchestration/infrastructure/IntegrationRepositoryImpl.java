package com.aixone.metacenter.integrationorchestration.infrastructure;

import com.aixone.metacenter.integrationorchestration.application.dto.IntegrationQuery;
import com.aixone.metacenter.integrationorchestration.domain.Integration;
import com.aixone.metacenter.integrationorchestration.domain.IntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class IntegrationRepositoryImpl implements IntegrationRepository {

    @Autowired
    private IntegrationJpaRepository integrationJpaRepository;

    @Override
    public Integration save(Integration integration) {
        return integrationJpaRepository.save(integration);
    }

    @Override
    public List<Integration> saveAll(List<Integration> integrations) {
        return integrationJpaRepository.saveAll(integrations);
    }

    @Override
    public List<Integration> findAll() {
        return integrationJpaRepository.findAll();
    }

    @Override
    public List<Integration> findByIds(List<Long> ids) {
        return integrationJpaRepository.findAllById(ids);
    }

    @Override
    public List<Integration> findByTenantId(String tenantId) {
        return integrationJpaRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Integration> findByStatus(String status) {
        return integrationJpaRepository.findByStatus(status);
    }

    @Override
    public List<Integration> findByIntegrationType(String integrationType) {
        return integrationJpaRepository.findByIntegrationType(integrationType);
    }

    @Override
    public Page<Integration> findByQuery(IntegrationQuery query, Pageable pageable) {
        Specification<Integration> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getTenantId() != null && !query.getTenantId().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tenantId"), query.getTenantId()));
            }

            if (query.getName() != null && !query.getName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + query.getName() + "%"));
            }

            if (query.getDisplayName() != null && !query.getDisplayName().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("displayName"), "%" + query.getDisplayName() + "%"));
            }

            if (query.getDescription() != null && !query.getDescription().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("description"), "%" + query.getDescription() + "%"));
            }

            if (query.getIntegrationType() != null && !query.getIntegrationType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("integrationType"), query.getIntegrationType()));
            }

            if (query.getSourceSystem() != null && !query.getSourceSystem().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("sourceSystem"), query.getSourceSystem()));
            }

            if (query.getTargetSystem() != null && !query.getTargetSystem().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("targetSystem"), query.getTargetSystem()));
            }

            if (query.getStatuses() != null && !query.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(query.getStatuses()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return integrationJpaRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        integrationJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Integration> integrations) {
        integrationJpaRepository.deleteAll(integrations);
    }

    @Override
    public boolean existsById(Long id) {
        return integrationJpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return integrationJpaRepository.count();
    }
}
