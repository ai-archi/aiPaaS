package com.aixone.tech.auth.config;

import com.aixone.tech.auth.authorization.domain.model.Permission;
import com.aixone.tech.auth.authorization.domain.model.Role;
import com.aixone.tech.auth.authorization.domain.model.UserRole;
import com.aixone.tech.auth.authorization.domain.model.AbacPolicy;
import com.aixone.tech.auth.authorization.domain.repository.PermissionRepository;
import com.aixone.tech.auth.authorization.domain.repository.RoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.UserRoleRepository;
import com.aixone.tech.auth.authorization.domain.repository.AbacPolicyRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 测试数据配置
 */
@TestConfiguration
public class TestPermissionDataConfig {
    
    @Bean
    @Primary
    public PermissionRepository testPermissionRepository() {
        return new PermissionRepository() {
            private final Map<String, Permission> permissions = new HashMap<>();
            
            {
                // 初始化权限数据
                permissions.put("perm_user_read", new Permission("perm_user_read", "default", "用户查看", "user:profile", "read", "查看用户基本信息"));
                permissions.put("perm_user_write", new Permission("perm_user_write", "default", "用户编辑", "user:profile", "write", "编辑用户基本信息"));
                permissions.put("perm_user_delete", new Permission("perm_user_delete", "default", "用户删除", "user:profile", "delete", "删除用户"));
                permissions.put("perm_user_list", new Permission("perm_user_list", "default", "用户列表", "user:list", "read", "查看用户列表"));
                permissions.put("perm_system_config", new Permission("perm_system_config", "default", "系统配置", "system:config", "write", "系统配置管理"));
                permissions.put("perm_system_log", new Permission("perm_system_log", "default", "系统日志", "system:log", "read", "查看系统日志"));
                permissions.put("perm_system_monitor", new Permission("perm_system_monitor", "default", "系统监控", "system:monitor", "read", "系统监控查看"));
                permissions.put("perm_role_read", new Permission("perm_role_read", "default", "角色查看", "role:list", "read", "查看角色列表"));
                permissions.put("perm_role_write", new Permission("perm_role_write", "default", "角色管理", "role:manage", "write", "角色创建和编辑"));
                permissions.put("perm_permission_read", new Permission("perm_permission_read", "default", "权限查看", "permission:list", "read", "查看权限列表"));
                permissions.put("perm_permission_write", new Permission("perm_permission_write", "default", "权限管理", "permission:manage", "write", "权限创建和编辑"));
                permissions.put("perm_tenant_read", new Permission("perm_tenant_read", "default", "租户查看", "tenant:info", "read", "查看租户信息"));
                permissions.put("perm_tenant_write", new Permission("perm_tenant_write", "default", "租户管理", "tenant:manage", "write", "租户配置管理"));
                permissions.put("perm_api_auth", new Permission("perm_api_auth", "default", "认证API", "api:auth", "access", "访问认证相关API"));
                permissions.put("perm_api_user", new Permission("perm_api_user", "default", "用户API", "api:user", "access", "访问用户相关API"));
                permissions.put("perm_api_admin", new Permission("perm_api_admin", "default", "管理API", "api:admin", "access", "访问管理相关API"));
            }
            
            @Override
            public List<Permission> findByTenantIdAndPermissionIdIn(String tenantId, List<String> permissionIds) {
                return permissionIds.stream()
                    .map(permissions::get)
                    .filter(Objects::nonNull)
                    .filter(p -> p.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public List<Permission> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action) {
                return permissions.values().stream()
                    .filter(p -> p.getTenantId().equals(tenantId) && 
                               p.getResource().equals(resource) && 
                               p.getAction().equals(action))
                    .toList();
            }
            
            @Override
            public Permission save(Permission permission) {
                permissions.put(permission.getPermissionId(), permission);
                return permission;
            }
            
            @Override
            public Permission findByTenantIdAndPermissionId(String tenantId, String permissionId) {
                Permission permission = permissions.get(permissionId);
                return (permission != null && permission.getTenantId().equals(tenantId)) ? permission : null;
            }
            
            @Override
            public void deleteByTenantIdAndPermissionId(String tenantId, String permissionId) {
                permissions.remove(permissionId);
            }

            @Override
            public List<Permission> findByTenantId(String tenantId) {
                return permissions.values().stream()
                    .filter(p -> p.getTenantId().equals(tenantId))
                    .toList();
            }

            @Override
            public boolean existsByTenantIdAndName(String tenantId, String name) {
                return permissions.values().stream()
                    .anyMatch(p -> p.getTenantId().equals(tenantId) && p.getName().equals(name));
            }
        };
    }
    
    @Bean
    @Primary
    public RoleRepository testRoleRepository() {
        return new RoleRepository() {
            private final Map<String, Role> roles = new HashMap<>();
            
            {
                // 初始化角色数据
                roles.put("role_admin", new Role("role_admin", "default", "系统管理员", "拥有所有权限", 
                    List.of("perm_user_read", "perm_user_write", "perm_user_delete", "perm_user_list",
                           "perm_system_config", "perm_system_log", "perm_system_monitor",
                           "perm_role_read", "perm_role_write", "perm_permission_read", "perm_permission_write",
                           "perm_tenant_read", "perm_tenant_write", "perm_api_auth", "perm_api_user", "perm_api_admin")));
                
                roles.put("role_user_manager", new Role("role_user_manager", "default", "用户管理员", "负责用户管理", 
                    List.of("perm_user_read", "perm_user_write", "perm_user_list", "perm_role_read", "perm_api_user")));
                
                roles.put("role_operator", new Role("role_operator", "default", "操作员", "具有基本操作权限", 
                    List.of("perm_user_read", "perm_system_log", "perm_api_auth")));
                
                roles.put("role_viewer", new Role("role_viewer", "default", "查看者", "只具有查看权限", 
                    List.of("perm_user_read", "perm_user_list", "perm_system_log", "perm_role_read", "perm_permission_read")));
                
                roles.put("role_guest", new Role("role_guest", "default", "访客", "访客角色", 
                    List.of("perm_user_read")));
            }
            
            @Override
            public List<Role> findByTenantIdAndRoleIdIn(String tenantId, List<String> roleIds) {
                return roleIds.stream()
                    .map(roles::get)
                    .filter(Objects::nonNull)
                    .filter(r -> r.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public Role save(Role role) {
                roles.put(role.getRoleId(), role);
                return role;
            }
            
            @Override
            public Role findByTenantIdAndRoleId(String tenantId, String roleId) {
                Role role = roles.get(roleId);
                return (role != null && role.getTenantId().equals(tenantId)) ? role : null;
            }
            
            @Override
            public List<Role> findByTenantId(String tenantId) {
                return roles.values().stream()
                    .filter(r -> r.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public void deleteByTenantIdAndRoleId(String tenantId, String roleId) {
                roles.remove(roleId);
            }

            @Override
            public boolean existsByTenantIdAndName(String tenantId, String name) {
                return roles.values().stream()
                    .anyMatch(r -> r.getTenantId().equals(tenantId) && r.getName().equals(name));
            }
        };
    }
    
    @Bean
    @Primary
    public UserRoleRepository testUserRoleRepository() {
        return new UserRoleRepository() {
            private final Map<String, List<UserRole>> userRoles = new HashMap<>();
            
            {
                // 初始化用户角色关联数据
                userRoles.put("admin", List.of(new UserRole("ur_admin_001", "default", "admin", "role_admin")));
                userRoles.put("user_manager", List.of(new UserRole("ur_manager_001", "default", "user_manager", "role_user_manager")));
                userRoles.put("operator", List.of(new UserRole("ur_operator_001", "default", "operator", "role_operator")));
                userRoles.put("viewer", List.of(new UserRole("ur_viewer_001", "default", "viewer", "role_viewer")));
                userRoles.put("guest", List.of(new UserRole("ur_guest_001", "default", "guest", "role_guest")));
            }
            
            @Override
            public List<UserRole> findByTenantIdAndUserId(String tenantId, String userId) {
                return userRoles.getOrDefault(userId, Collections.emptyList()).stream()
                    .filter(ur -> ur.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public UserRole save(UserRole userRole) {
                return userRole;
            }
            
            @Override
            public List<UserRole> findByTenantIdAndRoleId(String tenantId, String roleId) {
                return userRoles.values().stream()
                    .flatMap(List::stream)
                    .filter(ur -> ur.getTenantId().equals(tenantId) && ur.getRoleId().equals(roleId))
                    .toList();
            }
            
            @Override
            public void deleteByTenantIdAndUserId(String tenantId, String userId) {
                userRoles.remove(userId);
            }
            
            @Override
            public void deleteByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId) {
                List<UserRole> roles = userRoles.get(userId);
                if (roles != null) {
                    roles.removeIf(ur -> ur.getRoleId().equals(roleId));
                }
            }

            @Override
            public boolean existsByTenantIdAndUserIdAndRoleId(String tenantId, String userId, String roleId) {
                List<UserRole> roles = userRoles.get(userId);
                return roles != null && roles.stream()
                    .anyMatch(ur -> ur.getTenantId().equals(tenantId) && ur.getRoleId().equals(roleId));
            }
        };
    }
    
    @Bean
    @Primary
    public AbacPolicyRepository testAbacPolicyRepository() {
        return new AbacPolicyRepository() {
            private final Map<String, List<AbacPolicy>> policies = new HashMap<>();
            
            {
                // 初始化ABAC策略数据
                Map<String, Object> workHoursAttributes = new HashMap<>();
                workHoursAttributes.put("timezone", "Asia/Shanghai");
                workHoursAttributes.put("enabled", "true");
                
                Map<String, Object> deptIsolationAttributes = new HashMap<>();
                deptIsolationAttributes.put("strict_mode", "true");
                deptIsolationAttributes.put("cross_dept_allowed", "false");
                
                Map<String, Object> seniorLevelAttributes = new HashMap<>();
                seniorLevelAttributes.put("min_level", "3");
                seniorLevelAttributes.put("level_type", "seniority");
                
                Map<String, Object> ipWhitelistAttributes = new HashMap<>();
                ipWhitelistAttributes.put("whitelist_type", "cidr");
                ipWhitelistAttributes.put("fallback_action", "deny");
                
                policies.put("user:profile:read", List.of(
                    new AbacPolicy("policy_department_isolation", "default", "部门隔离策略", "用户只能访问同部门的数据", 
                        "user:profile", "read", "user.department == resource.department", deptIsolationAttributes)
                ));
                
                policies.put("user:profile:write", List.of(
                    new AbacPolicy("policy_work_hours", "default", "工作时间访问策略", "限制在工作时间（9:00-18:00）内访问敏感资源", 
                        "user:profile", "write", "time >= 09:00 AND time <= 18:00", workHoursAttributes)
                ));
                
                policies.put("system:config:read", List.of(
                    new AbacPolicy("policy_senior_level", "default", "高级别用户策略", "高级别用户可以访问更多资源", 
                        "system:config", "read", "user.level >= 3", seniorLevelAttributes)
                ));
                
                policies.put("api:admin:access", List.of(
                    new AbacPolicy("policy_ip_whitelist", "default", "IP白名单策略", "只允许特定IP访问管理功能", 
                        "api:admin", "access", "client_ip IN [\"192.168.1.0/24\", \"10.0.0.0/8\"]", ipWhitelistAttributes)
                ));
            }
            
            @Override
            public List<AbacPolicy> findByTenantIdAndResourceAndAction(String tenantId, String resource, String action) {
                String key = resource + ":" + action;
                return policies.getOrDefault(key, Collections.emptyList()).stream()
                    .filter(p -> p.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public AbacPolicy save(AbacPolicy policy) {
                return policy;
            }
            
            @Override
            public AbacPolicy findByTenantIdAndPolicyId(String tenantId, String policyId) {
                return policies.values().stream()
                    .flatMap(List::stream)
                    .filter(p -> p.getTenantId().equals(tenantId) && p.getPolicyId().equals(policyId))
                    .findFirst()
                    .orElse(null);
            }
            
            @Override
            public List<AbacPolicy> findByTenantId(String tenantId) {
                return policies.values().stream()
                    .flatMap(List::stream)
                    .filter(p -> p.getTenantId().equals(tenantId))
                    .toList();
            }
            
            @Override
            public void deleteByTenantIdAndPolicyId(String tenantId, String policyId) {
                policies.values().forEach(list -> list.removeIf(p -> p.getPolicyId().equals(policyId)));
            }

            @Override
            public boolean existsByTenantIdAndName(String tenantId, String name) {
                return policies.values().stream()
                    .flatMap(List::stream)
                    .anyMatch(p -> p.getTenantId().equals(tenantId) && p.getName().equals(name));
            }
        };
    }
}
