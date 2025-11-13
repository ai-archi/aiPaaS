package com.aixone.directory.organization.application;

import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import com.aixone.common.api.PageRequest;
import com.aixone.common.api.PageResult;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentJpaRepository;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentMapper;
import com.aixone.directory.organization.application.DepartmentDtoMapper;
import com.aixone.directory.organization.application.dto.DepartmentDto;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import jakarta.persistence.criteria.Predicate;

@Service
public class DepartmentApplicationService {

    private final DepartmentJpaRepository departmentJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final DepartmentMapper departmentMapper;
    private final DepartmentDtoMapper departmentDtoMapper;

    public DepartmentApplicationService(DepartmentJpaRepository departmentJpaRepository, UserJpaRepository userJpaRepository, DepartmentMapper departmentMapper, DepartmentDtoMapper departmentDtoMapper) {
        this.departmentJpaRepository = departmentJpaRepository;
        this.userJpaRepository = userJpaRepository;
        this.departmentMapper = departmentMapper;
        this.departmentDtoMapper = departmentDtoMapper;
    }

    @Transactional
    public void assignUsersToDepartment(String departmentId, Set<String> userIds) {
        DepartmentDbo department = departmentJpaRepository.findById(departmentId).orElseThrow();
        Set<UserDbo> users = new java.util.HashSet<>(userJpaRepository.findAllById(userIds));
        department.getUsers().addAll(users);
        departmentJpaRepository.save(department);
    }

    @Transactional
    public void removeUsersFromDepartment(String departmentId, Set<String> userIds) {
        DepartmentDbo department = departmentJpaRepository.findById(departmentId).orElseThrow();
        department.getUsers().removeIf(u -> userIds.contains(u.getId()));
        departmentJpaRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> getDepartments(String tenantId) {
        return departmentJpaRepository.findByTenantId(tenantId).stream()
                .map(dbo -> {
                    // 将 DepartmentDbo 转换为 Department 聚合，再转换为 DepartmentDto
                    com.aixone.directory.organization.domain.aggregate.Department department = departmentMapper.toDomain(dbo);
                    return departmentDtoMapper.toDto(department);
                })
                .collect(Collectors.toList());
    }

    /**
     * 分页查询部门列表（支持过滤）
     */
    @Transactional(readOnly = true)
    public PageResult<DepartmentDto> findDepartments(PageRequest pageRequest, String tenantId, String name, String orgId, String parentId) {
        // 验证 tenantId 不能为空
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("租户ID不能为空");
        }
        
        // 构建查询规格
        Specification<DepartmentDbo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new java.util.ArrayList<>();
            
            // 必须按租户ID过滤
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            
            // 支持name过滤
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            
            // 支持orgId过滤
            if (StringUtils.hasText(orgId)) {
                predicates.add(cb.equal(root.get("organization").get("id"), orgId));
            }
            
            // 支持parentId过滤
            if (StringUtils.hasText(parentId)) {
                if ("null".equalsIgnoreCase(parentId)) {
                    // 查询根部门（parentId为null）
                    predicates.add(cb.isNull(root.get("parentId")));
                } else {
                    // 查询指定父部门的子部门
                    predicates.add(cb.equal(root.get("parentId"), parentId));
                }
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        // 构建排序：默认按创建时间倒序
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by("createdAt").descending();
        
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
            pageRequest.getPageNum() - 1, // JPA 页码从 0 开始
            pageRequest.getPageSize(),
            sort
        );
        
        Page<DepartmentDbo> page = departmentJpaRepository.findAll(spec, pageable);
        List<DepartmentDto> content = page.getContent().stream()
                .map(dbo -> {
                    // 将 DepartmentDbo 转换为 Department 聚合，再转换为 DepartmentDto
                    com.aixone.directory.organization.domain.aggregate.Department department = departmentMapper.toDomain(dbo);
                    return departmentDtoMapper.toDto(department);
                })
                .collect(Collectors.toList());
        
        return PageResult.of(page.getTotalElements(), pageRequest, content);
    }
} 