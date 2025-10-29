-- 插入测试菜单数据
-- 注意：type 字段使用小写格式（menu, menu_dir, button），与前端期望一致
-- render_type 字段标识渲染方式：tab、iframe、link
-- component 字段需要使用 /src/views/backend/ 前缀的完整路径
INSERT INTO wb_menu (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- 根菜单 (使用默认租户UUID '00000000-0000-0000-0000-000000000000')
    ('11111111-1111-1111-1111-111111111111', '00000000-0000-0000-0000-000000000000', NULL, 'Dashboard', '仪表盘', '/admin/dashboard/index', 'fa fa-dashboard', 'menu', 'tab', '/src/views/backend/dashboard.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222222', '00000000-0000-0000-0000-000000000000', NULL, 'Settings', '设置', '/admin/settings', 'fa fa-gear', 'menu', 'tab', '/src/views/backend/login.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('33333333-3333-3333-3333-333333333333', '00000000-0000-0000-0000-000000000000', NULL, 'User Management', '用户管理', '/admin/users', 'fa fa-users', 'menu', 'tab', '/src/views/backend/auth-integration-test.vue', NULL, false, 3, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

