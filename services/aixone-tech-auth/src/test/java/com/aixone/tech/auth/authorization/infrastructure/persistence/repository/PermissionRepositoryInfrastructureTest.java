package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Permission Repository 基础设施层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.aixone.tech.auth")
public class PermissionRepositoryInfrastructureTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    public void testSaveAndFindPermission() {
        // Given
        Permission permission = new Permission();
        permission.setPermissionId("perm-1");
        permission.setName("READ_USER");
        permission.setResource("user");
        permission.setAction("read");
        permission.setDescription("读取用户信息");
        permission.setTenantId("test-tenant");
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        // When
        Permission savedPermission = permissionRepository.save(permission);
        List<Permission> foundPermissions = permissionRepository.findByTenantIdAndPermissionIdIn(
                "test-tenant", List.of("perm-1"));

        // Then
        assertThat(savedPermission).isNotNull();
        assertThat(savedPermission.getPermissionId()).isEqualTo("perm-1");
        assertThat(foundPermissions).hasSize(1);
        assertThat(foundPermissions.get(0).getName()).isEqualTo("READ_USER");
    }

    @Test
    public void testFindByTenantIdAndPermissionIdIn() {
        // Given
        Permission perm1 = new Permission();
        perm1.setPermissionId("perm-1");
        perm1.setName("READ_USER");
        perm1.setResource("user");
        perm1.setAction("read");
        perm1.setTenantId("test-tenant");
        perm1.setCreatedAt(LocalDateTime.now());
        perm1.setUpdatedAt(LocalDateTime.now());

        Permission perm2 = new Permission();
        perm2.setPermissionId("perm-2");
        perm2.setName("WRITE_USER");
        perm2.setResource("user");
        perm2.setAction("write");
        perm2.setTenantId("test-tenant");
        perm2.setCreatedAt(LocalDateTime.now());
        perm2.setUpdatedAt(LocalDateTime.now());

        Permission perm3 = new Permission();
        perm3.setPermissionId("perm-3");
        perm3.setName("READ_ORDER");
        perm3.setResource("order");
        perm3.setAction("read");
        perm3.setTenantId("other-tenant");
        perm3.setCreatedAt(LocalDateTime.now());
        perm3.setUpdatedAt(LocalDateTime.now());

        permissionRepository.save(perm1);
        permissionRepository.save(perm2);
        permissionRepository.save(perm3);

        // When
        List<Permission> permissions = permissionRepository.findByTenantIdAndPermissionIdIn(
                "test-tenant", List.of("perm-1", "perm-2"));

        // Then
        assertThat(permissions).hasSize(2);
        assertThat(permissions).extracting(Permission::getPermissionId)
                .containsExactlyInAnyOrder("perm-1", "perm-2");
    }

    @Test
    public void testFindByTenantIdAndResourceAndAction() {
        // Given
        Permission permission = new Permission();
        permission.setPermissionId("perm-1");
        permission.setName("READ_USER");
        permission.setResource("user");
        permission.setAction("read");
        permission.setTenantId("test-tenant");
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        permissionRepository.save(permission);

        // When
        List<Permission> permissions = permissionRepository.findByTenantIdAndResourceAndAction(
                "test-tenant", "user", "read");

        // Then
        assertThat(permissions).hasSize(1);
        assertThat(permissions.get(0).getPermissionId()).isEqualTo("perm-1");
        assertThat(permissions.get(0).getName()).isEqualTo("READ_USER");
    }

    @Test
    public void testFindByTenantIdAndResourceAndAction_NotFound() {
        // When
        List<Permission> permissions = permissionRepository.findByTenantIdAndResourceAndAction(
                "test-tenant", "non-existent", "read");

        // Then
        assertThat(permissions).isEmpty();
    }
}
