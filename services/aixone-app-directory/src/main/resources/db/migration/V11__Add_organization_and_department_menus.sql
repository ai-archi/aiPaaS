-- 添加组织管理和部门管理菜单
-- 本脚本保证幂等性：使用 ON CONFLICT DO UPDATE，可安全重复执行

-- 插入菜单数据
INSERT INTO menus (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- ========== 组织管理目录（根菜单）==========
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'default', NULL, 'OrganizationManagement', '组织管理', '/admin/organization', 'fa fa-sitemap', 'menu_dir', 'tab', NULL, NULL, false, 6, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- ========== 组织管理子菜单 ==========
    -- 组织列表
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'default', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'OrganizationList', '组织列表', '/admin/organization/organization/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/organization/organization/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- ========== 部门管理目录（根菜单）==========
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', 'default', NULL, 'DepartmentManagement', '部门管理', '/admin/department', 'fa fa-building-o', 'menu_dir', 'tab', NULL, NULL, false, 7, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- ========== 部门管理子菜单 ==========
    -- 部门列表
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', 'default', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'DepartmentList', '部门列表', '/admin/department/department/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/department/department/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO UPDATE SET 
    tenant_id = EXCLUDED.tenant_id,
    parent_id = EXCLUDED.parent_id,
    name = EXCLUDED.name,
    title = EXCLUDED.title,
    path = EXCLUDED.path,
    icon = EXCLUDED.icon,
    type = EXCLUDED.type,
    render_type = EXCLUDED.render_type,
    component = EXCLUDED.component,
    url = EXCLUDED.url,
    keepalive = EXCLUDED.keepalive,
    display_order = EXCLUDED.display_order,
    visible = EXCLUDED.visible,
    config = EXCLUDED.config,
    extend = EXCLUDED.extend,
    updated_at = CURRENT_TIMESTAMP;

