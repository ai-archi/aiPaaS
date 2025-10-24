package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRole Repository 基础设施层测试
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRoleRepositoryInfrastructureTest {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testSaveAndFindUserRole() {
        // Given
        UserRole userRole = new UserRole();
        userRole.setUserRoleId("user-role-1");
        userRole.setUserId("user-1");
        userRole.setRoleId("role-1");
        userRole.setTenantId("test-tenant");
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());

        // When
        UserRole savedUserRole =         userRoleRepository.save(userRole);
        List<UserRole> foundUserRoles = userRoleRepository.findByTenantIdAndUserId(
                "test-tenant", "user-1");

        // Then
        assertThat(savedUserRole).isNotNull();
        assertThat(savedUserRole.getUserRoleId()).isEqualTo("user-role-1");
        assertThat(foundUserRoles).hasSize(1);
        assertThat(foundUserRoles.get(0).getUserId()).isEqualTo("user-1");
        assertThat(foundUserRoles.get(0).getRoleId()).isEqualTo("role-1");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testFindByTenantIdAndUserId() {
        // Given
        UserRole userRole1 = new UserRole();
        userRole1.setUserRoleId("user-role-1");
        userRole1.setUserId("user-1");
        userRole1.setRoleId("role-1");
        userRole1.setTenantId("test-tenant");
        userRole1.setCreatedAt(LocalDateTime.now());
        userRole1.setUpdatedAt(LocalDateTime.now());

        UserRole userRole2 = new UserRole();
        userRole2.setUserRoleId("user-role-2");
        userRole2.setUserId("user-1");
        userRole2.setRoleId("role-2");
        userRole2.setTenantId("test-tenant");
        userRole2.setCreatedAt(LocalDateTime.now());
        userRole2.setUpdatedAt(LocalDateTime.now());

        UserRole userRole3 = new UserRole();
        userRole3.setUserRoleId("user-role-3");
        userRole3.setUserId("user-2");
        userRole3.setRoleId("role-1");
        userRole3.setTenantId("test-tenant");
        userRole3.setCreatedAt(LocalDateTime.now());
        userRole3.setUpdatedAt(LocalDateTime.now());

        userRoleRepository.save(userRole1);
        userRoleRepository.save(userRole2);
        userRoleRepository.save(userRole3);

        // When
        List<UserRole> userRoles = userRoleRepository.findByTenantIdAndUserId(
                "test-tenant", "user-1");

        // Then
        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRole::getUserRoleId)
                .containsExactlyInAnyOrder("user-role-1", "user-role-2");
    }

    @Test
    public void testFindByTenantIdAndUserId_NotFound() {
        // When
        List<UserRole> userRoles = userRoleRepository.findByTenantIdAndUserId(
                "test-tenant", "non-existent-user");

        // Then
        assertThat(userRoles).isEmpty();
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testSaveMultipleRolesForUser() {
        // Given
        UserRole adminRole = new UserRole();
        adminRole.setUserRoleId("user-admin-role");
        adminRole.setUserId("user-1");
        adminRole.setRoleId("admin-role");
        adminRole.setTenantId("test-tenant");
        adminRole.setCreatedAt(LocalDateTime.now());
        adminRole.setUpdatedAt(LocalDateTime.now());

        UserRole userRole = new UserRole();
        userRole.setUserRoleId("user-user-role");
        userRole.setUserId("user-1");
        userRole.setRoleId("user-role");
        userRole.setTenantId("test-tenant");
        userRole.setCreatedAt(LocalDateTime.now());
        userRole.setUpdatedAt(LocalDateTime.now());

        // When
        userRoleRepository.save(adminRole);
        userRoleRepository.save(userRole);

        List<UserRole> userRoles = userRoleRepository.findByTenantIdAndUserId(
                "test-tenant", "user-1");

        // Then
        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRole::getRoleId)
                .containsExactlyInAnyOrder("admin-role", "user-role");
    }
}
