-- 创建菜单表
CREATE TABLE IF NOT EXISTS menus (
    id VARCHAR(36) PRIMARY KEY,
    tenant_id VARCHAR(36) NOT NULL,
    parent_id VARCHAR(36),
    name VARCHAR(100) NOT NULL,
    title VARCHAR(100) NOT NULL,
    path VARCHAR(200) NOT NULL,
    icon VARCHAR(50),
    type VARCHAR(20) NOT NULL DEFAULT 'menu',
    render_type VARCHAR(50) DEFAULT 'tab',
    component VARCHAR(500),
    url VARCHAR(500),
    keepalive BOOLEAN DEFAULT false,
    display_order INTEGER DEFAULT 0,
    visible BOOLEAN DEFAULT true,
    config TEXT,
    extend VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES menus(id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_menus_tenant_id ON menus(tenant_id);
CREATE INDEX idx_menus_parent_id ON menus(parent_id);
CREATE INDEX idx_menus_tenant_parent ON menus(tenant_id, parent_id);
CREATE INDEX idx_menus_display_order ON menus(display_order);

-- 创建菜单角色关联表
CREATE TABLE IF NOT EXISTS menu_roles (
    menu_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id, role_id),
    CONSTRAINT fk_menu_roles_menu FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_roles_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_menu_roles_menu_id ON menu_roles(menu_id);
CREATE INDEX idx_menu_roles_role_id ON menu_roles(role_id);
