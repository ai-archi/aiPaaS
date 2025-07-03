package com.aixone.directory.organization.application;

import java.util.Set;
import java.util.UUID;
import com.aixone.directory.organization.infrastructure.persistence.dbo.DepartmentDbo;
import com.aixone.directory.organization.infrastructure.persistence.DepartmentJpaRepository;
import com.aixone.directory.user.infrastructure.persistence.dbo.UserDbo;
import com.aixone.directory.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.transaction.annotation.Transactional;

public class DepartmentApplicationService {

    private final DepartmentJpaRepository departmentJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public DepartmentApplicationService(DepartmentJpaRepository departmentJpaRepository, UserJpaRepository userJpaRepository) {
        this.departmentJpaRepository = departmentJpaRepository;
        this.userJpaRepository = userJpaRepository;
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
} 