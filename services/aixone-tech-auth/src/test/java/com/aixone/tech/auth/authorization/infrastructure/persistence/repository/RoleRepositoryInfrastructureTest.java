package com.aixone.tech.auth.authorization.infrastructure.persistence.repository;

import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Role Repository 基础设施层测试
 */
@DataJpaTest
@ActiveProfiles("test")
@ComponentScan(basePackages = "com.aixone.tech.auth")
public class RoleRepositoryInfrastructureTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveAndFindRole() {
        // Given
        Role role = new Role();
        role.setRoleId("role-1");
        role.setName("ADMIN");
        role.setDescription("管理员角色");
        role.setTenantId("test-tenant");
        role.setPermissions(List.of("perm-1", "perm-2"));
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        // When
        Role savedRole = roleRepository.save(role);
        List<Role> foundRoles = roleRepository.findByTenantIdAndRoleIdIn(
                "test-tenant", List.of("role-1"));

        // Then
        assertThat(savedRole).isNotNull();
        assertThat(savedRole.getRoleId()).isEqualTo("role-1");
        assertThat(foundRoles).hasSize(1);
        assertThat(foundRoles.get(0).getName()).isEqualTo("ADMIN");
        assertThat(foundRoles.get(0).getPermissions()).containsExactlyInAnyOrder("perm-1", "perm-2");
    }

    @Test
    public void testFindByTenantIdAndRoleIdIn() {
        // Given
        Role role1 = new Role();
        role1.setRoleId("role-1");
        role1.setName("ADMIN");
        role1.setDescription("管理员角色");
        role1.setTenantId("test-tenant");
        role1.setPermissions(List.of("perm-1", "perm-2"));
        role1.setCreatedAt(LocalDateTime.now());
        role1.setUpdatedAt(LocalDateTime.now());

        Role role2 = new Role();
        role2.setRoleId("role-2");
        role2.setName("USER");
        role2.setDescription("普通用户角色");
        role2.setTenantId("test-tenant");
        role2.setPermissions(List.of("perm-1"));
        role2.setCreatedAt(LocalDateTime.now());
        role2.setUpdatedAt(LocalDateTime.now());

        Role role3 = new Role();
        role3.setRoleId("role-3");
        role3.setName("GUEST");
        role3.setDescription("访客角色");
        role3.setTenantId("other-tenant");
        role3.setPermissions(List.of());
        role3.setCreatedAt(LocalDateTime.now());
        role3.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role1);
        roleRepository.save(role2);
        roleRepository.save(role3);

        // When
        List<Role> roles = roleRepository.findByTenantIdAndRoleIdIn(
                "test-tenant", List.of("role-1", "role-2"));

        // Then
        assertThat(roles).hasSize(2);
        assertThat(roles).extracting(Role::getRoleId)
                .containsExactlyInAnyOrder("role-1", "role-2");
    }

    @Test
    public void testFindByTenantIdAndRoleIdIn_EmptyList() {
        // When
        List<Role> roles = roleRepository.findByTenantIdAndRoleIdIn("test-tenant", List.of());

        // Then
        assertThat(roles).isEmpty();
    }

    @Test
    public void testUpdateRole() {
        // Given
        Role role = new Role();
        role.setRoleId("role-1");
        role.setName("ADMIN");
        role.setDescription("管理员角色");
        role.setTenantId("test-tenant");
        role.setPermissions(List.of("perm-1"));
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        roleRepository.save(role);

        // When - 更新角色权限
        role.setPermissions(List.of("perm-1", "perm-2", "perm-3"));
        role.setUpdatedAt(LocalDateTime.now());
        Role updatedRole = roleRepository.save(role);

        // Then
        assertThat(updatedRole.getPermissions()).hasSize(3);
        assertThat(updatedRole.getPermissions()).containsExactlyInAnyOrder("perm-1", "perm-2", "perm-3");
    }
}
