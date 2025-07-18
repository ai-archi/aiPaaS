package com.aixone.metacenter.permissionservice.infrastructure;

import com.aixone.metacenter.permissionservice.application.dto.PermissionQuery;
import com.aixone.metacenter.permissionservice.domain.Permission;
import com.aixone.metacenter.permissionservice.domain.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限仓储实现
 * 
 * @author aixone
 * @version 1.0.0
 * @since 2024-06-01
 */
@Repository
public class PermissionRepositoryImpl implements PermissionRepository {

    @Autowired
    private PermissionJpaRepository permissionJpaRepository;

    @Override
    public Permission save(Permission permission) {
        return permissionJpaRepository.save(permission);
    }

    @Override
    public List<Permission> saveAll(List<Permission> permissions) {
        return permissionJpaRepository.saveAll(permissions);
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll();
    }

    @Override
    public List<Permission> findByIds(List<Long> ids) {
        return permissionJpaRepository.findAllById(ids);
    }

    @Override
    public List<Permission> findByTenantId(String tenantId) {
        return permissionJpaRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Permission> findByStatus(String status) {
        return permissionJpaRepository.findByStatus(status);
    }

    @Override
    public List<Permission> findByRoleId(Long roleId) {
        return permissionJpaRepository.findByRoleId(roleId);
    }

    @Override
    public List<Permission> findByUserId(Long userId) {
        return permissionJpaRepository.findByUserId(userId);
    }

    @Override
    public Page<Permission> findByQuery(PermissionQuery query, Pageable pageable) {
        Specification<Permission> spec = (root, criteriaQuery, criteriaBuilder) -> {
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

            if (query.getPermissionType() != null && !query.getPermissionType().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("permissionType"), query.getPermissionType()));
            }

            if (query.getResource() != null && !query.getResource().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("resource"), query.getResource()));
            }

            if (query.getAction() != null && !query.getAction().isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("action"), query.getAction()));
            }

            if (query.getRoleId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("roleId"), query.getRoleId()));
            }

            if (query.getUserId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), query.getUserId()));
            }

            if (query.getStatuses() != null && !query.getStatuses().isEmpty()) {
                predicates.add(root.get("status").in(query.getStatuses()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return permissionJpaRepository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        permissionJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAll(List<Permission> permissions) {
        permissionJpaRepository.deleteAll(permissions);
    }

    @Override
    public boolean existsById(Long id) {
        return permissionJpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return permissionJpaRepository.count();
    }
} 