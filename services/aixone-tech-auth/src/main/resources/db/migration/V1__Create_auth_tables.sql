-- 认证授权服务数据库初始化脚本

-- 创建客户端表
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    redirect_uri VARCHAR(500),
    scopes TEXT,
    grant_types TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_clients_client_tenant UNIQUE (client_id, tenant_id)
);

-- 创建令牌表
CREATE TABLE IF NOT EXISTS tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建令牌黑名单表
CREATE TABLE IF NOT EXISTS token_blacklist (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(500) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建审计日志表
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGSERIAL PRIMARY KEY,
    log_id VARCHAR(255) NOT NULL UNIQUE,
    tenant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    resource VARCHAR(500),
    result VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    client_ip VARCHAR(50),
    user_agent TEXT
);

-- 创建验证码表
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGSERIAL PRIMARY KEY,
    phone VARCHAR(20),
    email VARCHAR(255),
    tenant_id VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- 创建租户配置表
CREATE TABLE IF NOT EXISTS tenant_configs (
    tenant_id VARCHAR(255) PRIMARY KEY,
    auth_config TEXT,
    oauth_config TEXT,
    security_config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建权限表
CREATE TABLE IF NOT EXISTS permissions (
    permission_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permissions_tenant_resource_action UNIQUE (tenant_id, resource, action)
);

-- 创建角色表
CREATE TABLE IF NOT EXISTS roles (
    role_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_tenant_name UNIQUE (tenant_id, name)
);

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id VARCHAR(255) NOT NULL,
    permission_id VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    user_role_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    role_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_roles_tenant_user_role UNIQUE (tenant_id, user_id, role_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- 创建ABAC策略表
CREATE TABLE IF NOT EXISTS abac_policies (
    policy_id VARCHAR(255) PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    condition TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建ABAC策略属性表
CREATE TABLE IF NOT EXISTS abac_policy_attributes (
    policy_id VARCHAR(255) NOT NULL,
    attribute_key VARCHAR(255) NOT NULL,
    attribute_value TEXT,
    PRIMARY KEY (policy_id, attribute_key),
    FOREIGN KEY (policy_id) REFERENCES abac_policies(policy_id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_clients_tenant_id ON clients(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tokens_tenant_id ON tokens(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tokens_user_id ON tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_tokens_client_id ON tokens(client_id);
CREATE INDEX IF NOT EXISTS idx_tokens_expires_at ON tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_tenant_id ON token_blacklist(tenant_id);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_expires_at ON token_blacklist(expires_at);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_token ON token_blacklist(token);
CREATE INDEX IF NOT EXISTS idx_audit_logs_tenant_id ON audit_logs(tenant_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_action ON audit_logs(action);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX IF NOT EXISTS idx_verification_codes_tenant_id ON verification_codes(tenant_id);
CREATE INDEX IF NOT EXISTS idx_verification_codes_expires_at ON verification_codes(expires_at);
CREATE INDEX IF NOT EXISTS idx_verification_codes_phone_tenant ON verification_codes(phone, tenant_id);
CREATE INDEX IF NOT EXISTS idx_verification_codes_email_tenant ON verification_codes(email, tenant_id);
CREATE INDEX IF NOT EXISTS idx_permissions_tenant_id ON permissions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_permissions_resource_action ON permissions(resource, action);
CREATE INDEX IF NOT EXISTS idx_roles_tenant_id ON roles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_tenant_id ON user_roles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_abac_policies_tenant_id ON abac_policies(tenant_id);
CREATE INDEX IF NOT EXISTS idx_abac_policies_resource_action ON abac_policies(resource, action);

-- 插入默认客户端数据
INSERT INTO clients (client_id, tenant_id, client_secret, redirect_uri, scopes, grant_types) 
VALUES ('default-client', 'default', 'default-secret', 'http://localhost:3000/callback', 'read write', 'authorization_code password refresh_token')
ON CONFLICT (client_id, tenant_id) DO NOTHING;

-- 插入默认租户配置
INSERT INTO tenant_configs (tenant_id, auth_config, oauth_config, security_config) 
VALUES ('default', '{"enabled": true}', '{"providers": ["alipay", "wechat"]}', '{"rateLimit": {"enabled": true, "maxAttempts": 5}}')
ON CONFLICT (tenant_id) DO NOTHING;

-- 插入默认权限数据
INSERT INTO permissions (permission_id, tenant_id, name, resource, action, description) VALUES
-- 用户管理权限
('perm_user_read', 'default', '用户查看', 'user:profile', 'read', '查看用户基本信息'),
('perm_user_write', 'default', '用户编辑', 'user:profile', 'write', '编辑用户基本信息'),
('perm_user_delete', 'default', '用户删除', 'user:profile', 'delete', '删除用户'),
('perm_user_list', 'default', '用户列表', 'user:list', 'read', '查看用户列表'),

-- 系统管理权限
('perm_system_config', 'default', '系统配置', 'system:config', 'write', '系统配置管理'),
('perm_system_log', 'default', '系统日志', 'system:log', 'read', '查看系统日志'),
('perm_system_monitor', 'default', '系统监控', 'system:monitor', 'read', '系统监控查看'),

-- 权限管理权限
('perm_role_read', 'default', '角色查看', 'role:list', 'read', '查看角色列表'),
('perm_role_write', 'default', '角色管理', 'role:manage', 'write', '角色创建和编辑'),
('perm_permission_read', 'default', '权限查看', 'permission:list', 'read', '查看权限列表'),
('perm_permission_write', 'default', '权限管理', 'permission:manage', 'write', '权限创建和编辑'),

-- 租户管理权限
('perm_tenant_read', 'default', '租户查看', 'tenant:info', 'read', '查看租户信息'),
('perm_tenant_write', 'default', '租户管理', 'tenant:manage', 'write', '租户配置管理'),

-- API访问权限
('perm_api_auth', 'default', '认证API', 'api:auth', 'access', '访问认证相关API'),
('perm_api_user', 'default', '用户API', 'api:user', 'access', '访问用户相关API'),
('perm_api_admin', 'default', '管理API', 'api:admin', 'access', '访问管理相关API')
ON CONFLICT (permission_id) DO NOTHING;

-- 插入默认角色数据
INSERT INTO roles (role_id, tenant_id, name, description) VALUES
('role_admin', 'default', '系统管理员', '拥有所有权限的系统管理员角色'),
('role_user_manager', 'default', '用户管理员', '负责用户管理的角色'),
('role_operator', 'default', '操作员', '具有基本操作权限的角色'),
('role_viewer', 'default', '查看者', '只具有查看权限的角色'),
('role_guest', 'default', '访客', '访客角色，权限最少')
ON CONFLICT (role_id) DO NOTHING;

-- 插入角色权限关联数据
INSERT INTO role_permissions (role_id, permission_id) VALUES
-- 系统管理员拥有所有权限
('role_admin', 'perm_user_read'),
('role_admin', 'perm_user_write'),
('role_admin', 'perm_user_delete'),
('role_admin', 'perm_user_list'),
('role_admin', 'perm_system_config'),
('role_admin', 'perm_system_log'),
('role_admin', 'perm_system_monitor'),
('role_admin', 'perm_role_read'),
('role_admin', 'perm_role_write'),
('role_admin', 'perm_permission_read'),
('role_admin', 'perm_permission_write'),
('role_admin', 'perm_tenant_read'),
('role_admin', 'perm_tenant_write'),
('role_admin', 'perm_api_auth'),
('role_admin', 'perm_api_user'),
('role_admin', 'perm_api_admin'),

-- 用户管理员权限
('role_user_manager', 'perm_user_read'),
('role_user_manager', 'perm_user_write'),
('role_user_manager', 'perm_user_list'),
('role_user_manager', 'perm_role_read'),
('role_user_manager', 'perm_api_user'),

-- 操作员权限
('role_operator', 'perm_user_read'),
('role_operator', 'perm_system_log'),
('role_operator', 'perm_api_auth'),

-- 查看者权限
('role_viewer', 'perm_user_read'),
('role_viewer', 'perm_user_list'),
('role_viewer', 'perm_system_log'),
('role_viewer', 'perm_role_read'),
('role_viewer', 'perm_permission_read'),

-- 访客权限
('role_guest', 'perm_user_read')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- 插入默认用户角色关联数据（示例用户）
INSERT INTO user_roles (user_role_id, tenant_id, user_id, role_id) VALUES
('ur_admin_001', 'default', 'admin', 'role_admin'),
('ur_manager_001', 'default', 'user_manager', 'role_user_manager'),
('ur_operator_001', 'default', 'operator', 'role_operator'),
('ur_viewer_001', 'default', 'viewer', 'role_viewer'),
('ur_guest_001', 'default', 'guest', 'role_guest')
ON CONFLICT (user_role_id) DO NOTHING;

-- 插入默认ABAC策略数据
INSERT INTO abac_policies (policy_id, tenant_id, name, description, resource, action, condition) VALUES
-- 工作时间访问策略
('policy_work_hours', 'default', '工作时间访问策略', '限制在工作时间（9:00-18:00）内访问敏感资源', 'user:profile', 'write', 'time >= 09:00 AND time <= 18:00'),

-- 部门隔离策略
('policy_department_isolation', 'default', '部门隔离策略', '用户只能访问同部门的数据', 'user:profile', 'read', 'user.department == resource.department'),

-- 高级别用户策略
('policy_senior_level', 'default', '高级别用户策略', '高级别用户可以访问更多资源', 'system:config', 'read', 'user.level >= 3'),

-- IP白名单策略
('policy_ip_whitelist', 'default', 'IP白名单策略', '只允许特定IP访问管理功能', 'api:admin', 'access', 'client_ip IN ["192.168.1.0/24", "10.0.0.0/8"]')
ON CONFLICT (policy_id) DO NOTHING;

-- 插入ABAC策略属性数据
INSERT INTO abac_policy_attributes (policy_id, attribute_key, attribute_value) VALUES
-- 工作时间策略属性
('policy_work_hours', 'timezone', 'Asia/Shanghai'),
('policy_work_hours', 'enabled', 'true'),

-- 部门隔离策略属性
('policy_department_isolation', 'strict_mode', 'true'),
('policy_department_isolation', 'cross_dept_allowed', 'false'),

-- 高级别用户策略属性
('policy_senior_level', 'min_level', '3'),
('policy_senior_level', 'level_type', 'seniority'),

-- IP白名单策略属性
('policy_ip_whitelist', 'whitelist_type', 'cidr'),
('policy_ip_whitelist', 'fallback_action', 'deny')
ON CONFLICT (policy_id, attribute_key) DO NOTHING;
