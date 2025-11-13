-- 创建权限规则表
-- 用于管理接口的权限验证规则（路径-权限映射）
CREATE TABLE IF NOT EXISTS permission_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    pattern VARCHAR(500) NOT NULL,
    permission VARCHAR(255) NOT NULL,
    description TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    priority INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permission_rules_tenant_pattern UNIQUE (tenant_id, pattern)
);

-- 创建权限规则方法关联表（支持一个规则对应多个HTTP方法）
CREATE TABLE IF NOT EXISTS permission_rule_methods (
    rule_id UUID NOT NULL,
    method VARCHAR(20) NOT NULL,
    PRIMARY KEY (rule_id, method),
    CONSTRAINT fk_permission_rule_methods_rule FOREIGN KEY (rule_id) REFERENCES permission_rules(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_permission_rules_tenant_id ON permission_rules(tenant_id);
CREATE INDEX IF NOT EXISTS idx_permission_rules_pattern ON permission_rules(pattern);
CREATE INDEX IF NOT EXISTS idx_permission_rules_permission ON permission_rules(permission);
CREATE INDEX IF NOT EXISTS idx_permission_rules_enabled ON permission_rules(enabled);
CREATE INDEX IF NOT EXISTS idx_permission_rules_priority ON permission_rules(priority DESC);

-- 添加外键约束（关联tenants表）
ALTER TABLE permission_rules 
    ADD CONSTRAINT fk_permission_rules_tenant 
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;

-- 添加注释
COMMENT ON TABLE permission_rules IS '权限规则表，用于管理接口的权限验证规则（路径-权限映射）';
COMMENT ON COLUMN permission_rules.id IS '权限规则ID';
COMMENT ON COLUMN permission_rules.tenant_id IS '租户ID';
COMMENT ON COLUMN permission_rules.pattern IS '路径模式，支持Ant路径匹配（**、*）';
COMMENT ON COLUMN permission_rules.permission IS '权限标识，格式：{resource}:{action} 或 admin:{resource}:{action}';
COMMENT ON COLUMN permission_rules.description IS '权限规则描述';
COMMENT ON COLUMN permission_rules.enabled IS '是否启用';
COMMENT ON COLUMN permission_rules.priority IS '优先级，数字越大优先级越高';
COMMENT ON TABLE permission_rule_methods IS '权限规则方法关联表，存储权限规则对应的HTTP方法';
COMMENT ON COLUMN permission_rule_methods.rule_id IS '权限规则ID';
COMMENT ON COLUMN permission_rule_methods.method IS 'HTTP方法（GET、POST、PUT、DELETE等）';

