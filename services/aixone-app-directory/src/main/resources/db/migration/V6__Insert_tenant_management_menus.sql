-- 插入租户管理菜单数据
-- 先创建默认租户（如果不存在）
INSERT INTO tenants (id, name, status, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'Default Tenant', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 插入菜单数据
INSERT INTO menus (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- 仪表盘菜单
    ('11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000000', NULL, 'Dashboard', '仪表盘', '/admin/dashboard/index', 'fa fa-dashboard', 'menu', 'tab', '/src/views/backend/dashboard.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户管理目录
    ('22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000000', NULL, 'TenantManagement', '租户管理', '/admin/tenant', 'fa fa-building', 'menu_dir', 'tab', NULL, NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户列表菜单
    ('33333333-3333-3333-3333-333333333333', '00000000-0000-0000-0000-000000000000', '22222222-2222-2222-2222-222222222222', 'TenantList', '租户列表', '/admin/tenant/tenant/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/tenant/tenant/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户组管理菜单
    ('44444444-4444-4444-4444-444444444444', '00000000-0000-0000-0000-000000000000', '22222222-2222-2222-2222-222222222222', 'TenantGroupList', '租户组管理', '/admin/tenant/group/index', 'fa fa-sitemap', 'menu', 'tab', '/src/views/backend/tenant/group/index.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 用户管理目录
    ('55555555-5555-5555-5555-555555555555', '00000000-0000-0000-0000-000000000000', NULL, 'UserManagement', '用户管理', '/admin/user', 'fa fa-users', 'menu_dir', 'tab', NULL, NULL, false, 3, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 用户列表菜单
    ('66666666-6666-6666-6666-666666666666', '00000000-0000-0000-0000-000000000000', '55555555-5555-5555-5555-555555555555', 'UserList', '用户列表', '/admin/user/user/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/user/user/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 用户组管理菜单
    ('77777777-7777-7777-7777-777777777777', '00000000-0000-0000-0000-000000000000', '55555555-5555-5555-5555-555555555555', 'UserGroupList', '用户组管理', '/admin/user/group/index', 'fa fa-sitemap', 'menu', 'tab', '/src/views/backend/user/group/index.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
