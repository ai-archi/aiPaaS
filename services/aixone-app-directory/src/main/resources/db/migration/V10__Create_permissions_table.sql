-- 创建权限表
-- 用于管理RBAC/ABAC权限数据
CREATE TABLE IF NOT EXISTS permissions (
    permission_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(255) NOT NULL,
    resource VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'FUNCTIONAL',
    description TEXT,
    abac_conditions JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permissions_tenant_code UNIQUE (tenant_id, code),
    CONSTRAINT uk_permissions_tenant_resource_action UNIQUE (tenant_id, resource, action)
);

-- 如果表已存在但缺少列，则添加缺失的列
DO $$
DECLARE
    v_row_count INTEGER;
BEGIN
    -- 检查并添加 code 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'code'
    ) THEN
        ALTER TABLE permissions ADD COLUMN code VARCHAR(255);
        -- 为现有数据生成唯一的 code（基于 name 和 permission_id）
        UPDATE permissions 
        SET code = LOWER(REPLACE(REPLACE(name, ' ', '_'), '-', '_')) || '_' || SUBSTRING(permission_id::TEXT, 1, 8)
        WHERE code IS NULL;
        -- 确保所有 code 都有值后再设置 NOT NULL
        SELECT COUNT(*) INTO v_row_count FROM permissions WHERE code IS NULL;
        IF v_row_count = 0 THEN
            ALTER TABLE permissions ALTER COLUMN code SET NOT NULL;
        END IF;
    END IF;
    
    -- 检查并添加 type 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'type'
    ) THEN
        ALTER TABLE permissions ADD COLUMN type VARCHAR(50) NOT NULL DEFAULT 'FUNCTIONAL';
    END IF;
    
    -- 检查并添加 abac_conditions 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'abac_conditions'
    ) THEN
        ALTER TABLE permissions ADD COLUMN abac_conditions JSONB;
    END IF;
END $$;

-- 创建角色权限关系表
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);

-- 如果 role_permissions 表已存在但缺少列，则添加缺失的列
DO $$
BEGIN
    -- 检查并添加 tenant_id 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'role_permissions' AND column_name = 'tenant_id'
    ) THEN
        ALTER TABLE role_permissions ADD COLUMN tenant_id VARCHAR(255);
        -- 尝试从关联的 permission 或 role 获取 tenant_id（如果可能）
        -- 如果无法获取，设置为默认值或允许 NULL（根据业务需求）
        UPDATE role_permissions rp
        SET tenant_id = (
            SELECT tenant_id FROM permissions p 
            WHERE p.permission_id = rp.permission_id 
            LIMIT 1
        )
        WHERE tenant_id IS NULL;
        -- 如果仍有 NULL 值，可以设置默认值或保持 NULL（根据业务需求）
        -- ALTER TABLE role_permissions ALTER COLUMN tenant_id SET NOT NULL;
    END IF;
    
    -- 检查并添加 created_at 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'role_permissions' AND column_name = 'created_at'
    ) THEN
        ALTER TABLE role_permissions ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- 创建索引（仅在列存在时创建）
DO $$
BEGIN
    -- permissions 表的索引
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'tenant_id'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_permissions_tenant_id ON permissions(tenant_id);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'code'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_permissions_code ON permissions(code);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'resource'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_permissions_resource ON permissions(resource);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'action'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_permissions_action ON permissions(action);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'permissions' AND column_name = 'type'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_permissions_type ON permissions(type);
    END IF;
    
    -- role_permissions 表的索引
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'role_permissions' AND column_name = 'role_id'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_role_permissions_role_id ON role_permissions(role_id);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'role_permissions' AND column_name = 'permission_id'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_role_permissions_permission_id ON role_permissions(permission_id);
    END IF;
    
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'role_permissions' AND column_name = 'tenant_id'
    ) THEN
CREATE INDEX IF NOT EXISTS idx_role_permissions_tenant_id ON role_permissions(tenant_id);
    END IF;
END $$;

-- 添加唯一约束（如果不存在且没有重复数据）
DO $$
DECLARE
    v_duplicate_count INTEGER;
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'uk_permissions_tenant_code'
    ) THEN
        -- 检查是否存在重复的 (tenant_id, code) 组合
        SELECT COUNT(*) INTO v_duplicate_count
        FROM (
            SELECT tenant_id, code, COUNT(*) as cnt
            FROM permissions
            WHERE code IS NOT NULL
            GROUP BY tenant_id, code
            HAVING COUNT(*) > 1
        ) duplicates;
        
        -- 如果没有重复数据，则添加唯一约束
        IF v_duplicate_count = 0 THEN
            ALTER TABLE permissions ADD CONSTRAINT uk_permissions_tenant_code UNIQUE (tenant_id, code);
        END IF;
    END IF;
END $$;

-- 添加外键约束（关联tenants表，如果不存在）
DO $$
BEGIN
    -- permissions 表的外键约束
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_permissions_tenant'
    ) THEN
        -- 确保 tenant_id 列存在，并且 tenants 表存在且有 id 列
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'permissions' AND column_name = 'tenant_id'
        ) AND EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'tenants' AND column_name = 'id'
        ) THEN
ALTER TABLE permissions 
    ADD CONSTRAINT fk_permissions_tenant 
    FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE;
        END IF;
    END IF;
    
    -- role_permissions 表的外键约束
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_role_permissions_role'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'role_permissions' AND column_name = 'role_id'
        ) AND EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'roles' AND column_name = 'role_id'
        ) THEN
            ALTER TABLE role_permissions 
                ADD CONSTRAINT fk_role_permissions_role 
                FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE;
        END IF;
    END IF;
    
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_role_permissions_permission'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'role_permissions' AND column_name = 'permission_id'
        ) AND EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'permissions' AND column_name = 'permission_id'
        ) THEN
            ALTER TABLE role_permissions 
                ADD CONSTRAINT fk_role_permissions_permission 
                FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE;
        END IF;
    END IF;
END $$;

-- 添加注释
COMMENT ON TABLE permissions IS '权限表，用于管理RBAC/ABAC权限数据';
COMMENT ON COLUMN permissions.permission_id IS '权限ID';
COMMENT ON COLUMN permissions.tenant_id IS '租户ID';
COMMENT ON COLUMN permissions.name IS '权限名称';
COMMENT ON COLUMN permissions.code IS '权限编码（唯一）';
COMMENT ON COLUMN permissions.resource IS '资源标识';
COMMENT ON COLUMN permissions.action IS '操作标识（read、write、delete等）';
COMMENT ON COLUMN permissions.type IS '权限类型：FUNCTIONAL（功能权限）/DATA（数据权限）';
COMMENT ON COLUMN permissions.description IS '权限描述';
COMMENT ON COLUMN permissions.abac_conditions IS 'ABAC条件（JSON格式）';
COMMENT ON TABLE role_permissions IS '角色权限关系表';
COMMENT ON COLUMN role_permissions.role_id IS '角色ID';
COMMENT ON COLUMN role_permissions.permission_id IS '权限ID';
COMMENT ON COLUMN role_permissions.tenant_id IS '租户ID';

