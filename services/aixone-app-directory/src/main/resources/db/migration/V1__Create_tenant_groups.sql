-- 创建租户组表
CREATE TABLE IF NOT EXISTS tenant_groups (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id VARCHAR(36),
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_tenant_group_parent FOREIGN KEY (parent_id) REFERENCES tenant_groups(id) ON DELETE SET NULL
);

-- 创建索引
CREATE INDEX idx_tenant_groups_parent_id ON tenant_groups(parent_id);
CREATE INDEX idx_tenant_groups_status ON tenant_groups(status);

-- 为 tenants 表添加 group_id 字段（如果不存在）
DO $$ 
BEGIN 
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='tenants' AND column_name='group_id') THEN
        ALTER TABLE tenants ADD COLUMN group_id VARCHAR(36);
        CREATE INDEX idx_tenants_group_id ON tenants(group_id);
        ALTER TABLE tenants ADD CONSTRAINT fk_tenants_group 
            FOREIGN KEY (group_id) REFERENCES tenant_groups(id) ON DELETE SET NULL;
    END IF;
END $$;

