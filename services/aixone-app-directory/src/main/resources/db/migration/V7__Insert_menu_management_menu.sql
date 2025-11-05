-- 插入菜单管理菜单数据
INSERT INTO menus (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- 菜单管理菜单项
    ('88888888-8888-8888-8888-888888888888', '00000000-0000-0000-0000-000000000000', NULL, 'MenuManagement', '菜单规则管理', '/admin/menu', 'fa fa-list-alt', 'menu', 'tab', '/src/views/backend/menu/index.vue', NULL, false, 4, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

