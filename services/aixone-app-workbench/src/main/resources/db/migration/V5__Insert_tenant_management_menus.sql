-- 插入租户管理菜单数据到工作台服务
-- 先创建默认租户（如果不存在）
INSERT INTO tenants (id, name, status, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'Default Tenant', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 插入租户管理菜单数据
INSERT INTO wb_menu (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- 租户管理目录
    ('44444444-4444-4444-4444-444444444444', '00000000-0000-0000-0000-000000000000', NULL, 'TenantManagement', '租户管理', '/admin/tenant', 'fa fa-building', 'menu_dir', 'tab', NULL, NULL, false, 4, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户列表菜单
    ('55555555-5555-5555-5555-555555555555', '00000000-0000-0000-0000-000000000000', '44444444-4444-4444-4444-444444444444', 'TenantList', '租户列表', '/admin/tenant/tenant/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/tenant/tenant/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户组列表菜单
    ('66666666-6666-6666-6666-666666666666', '00000000-0000-0000-0000-000000000000', '44444444-4444-4444-4444-444444444444', 'TenantGroupList', '租户组管理', '/admin/tenant/group/index', 'fa fa-sitemap', 'menu', 'tab', '/src/views/backend/tenant/group/index.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
