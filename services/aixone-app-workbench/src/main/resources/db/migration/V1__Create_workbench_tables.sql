-- AixOne 工作台服务数据库表结构
-- Version: 1.0.0
-- Author: AixOne Team

-- ============================================================
-- 菜单表
-- ============================================================
CREATE TABLE IF NOT EXISTS wb_menu (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    parent_id UUID,
    name VARCHAR(100),
    title VARCHAR(100),
    path VARCHAR(200),
    icon VARCHAR(50),
    type VARCHAR(20) NOT NULL,
    menu_type VARCHAR(50),
    component VARCHAR(500),
    url VARCHAR(500),
    keepalive BOOLEAN,
    display_order INTEGER,
    visible BOOLEAN DEFAULT true,
    config TEXT,
    extend VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_menu_parent FOREIGN KEY (parent_id) REFERENCES wb_menu(id) ON DELETE CASCADE
);

-- 菜单表索引
CREATE INDEX idx_wb_menu_tenant_id ON wb_menu(tenant_id);
CREATE INDEX idx_wb_menu_parent_id ON wb_menu(parent_id);
CREATE INDEX idx_wb_menu_tenant_parent ON wb_menu(tenant_id, parent_id);

-- ============================================================
-- 用户菜单个性化配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS wb_user_menu_custom (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    menu_id UUID NOT NULL,
    config TEXT,
    is_quick_entry BOOLEAN DEFAULT false,
    custom_order INTEGER,
    is_hidden BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_menu UNIQUE (user_id, menu_id),
    CONSTRAINT fk_menu_custom_menu FOREIGN KEY (menu_id) REFERENCES wb_menu(id) ON DELETE CASCADE
);

-- 用户菜单个性化配置表索引
CREATE INDEX idx_wb_user_menu_custom_user_id ON wb_user_menu_custom(user_id);
CREATE INDEX idx_wb_user_menu_custom_user_tenant ON wb_user_menu_custom(user_id, tenant_id);

-- ============================================================
-- 用户仪表盘配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS wb_user_dashboard (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    name VARCHAR(100),
    layout JSONB,
    components JSONB,
    config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户仪表盘配置表索引
CREATE INDEX idx_wb_user_dashboard_user_id ON wb_user_dashboard(user_id);
CREATE INDEX idx_wb_user_dashboard_user_tenant ON wb_user_dashboard(user_id, tenant_id);

-- ============================================================
-- 用户快捷入口表
-- ============================================================
CREATE TABLE IF NOT EXISTS wb_user_quick_entry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    entry_id UUID NOT NULL,
    menu_id UUID,
    name VARCHAR(100),
    icon VARCHAR(50),
    display_order INTEGER,
    config TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户快捷入口表索引
CREATE INDEX idx_wb_user_quick_entry_user_id ON wb_user_quick_entry(user_id);
CREATE INDEX idx_wb_user_quick_entry_user_tenant ON wb_user_quick_entry(user_id, tenant_id);

-- ============================================================
-- 用户偏好配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS wb_user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    preference_type VARCHAR(50) NOT NULL,
    preference_value JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 用户偏好配置表索引
CREATE INDEX idx_wb_user_preferences_user_id ON wb_user_preferences(user_id);
CREATE INDEX idx_wb_user_preferences_user_tenant ON wb_user_preferences(user_id, tenant_id);
CREATE INDEX idx_wb_user_preferences_type ON wb_user_preferences(preference_type);

COMMENT ON TABLE wb_menu IS '菜单表 - 存储菜单主数据';
COMMENT ON TABLE wb_user_menu_custom IS '用户菜单个性化配置表 - 存储用户对菜单的个性化配置';
COMMENT ON TABLE wb_user_dashboard IS '用户仪表盘配置表 - 存储用户的仪表盘布局和组件';
COMMENT ON TABLE wb_user_quick_entry IS '用户快捷入口表 - 存储用户的快捷入口配置';
COMMENT ON TABLE wb_user_preferences IS '用户偏好配置表 - 存储用户的主题、语言等偏好';

