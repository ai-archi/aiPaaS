-- 初始化菜单数据
-- 整合 V6、V7 的菜单数据，参考 Workbench 服务的菜单数据，但不依赖 wb_menu 表
-- 本脚本保证幂等性：使用 ON CONFLICT DO UPDATE，可安全重复执行
-- 
-- 包含的菜单：
--   - V6 中的菜单：仪表盘、租户管理目录、租户列表、租户组管理、用户管理目录、用户列表、用户组管理
--   - V7 中的菜单：菜单管理
--   - 新增菜单：设置
-- 
-- 先确保默认租户存在（支持两种格式：UUID 和 "default"）
-- 注意：如果 name 有唯一约束，使用不同的 name
INSERT INTO tenants (id, name, status, created_at, updated_at)
VALUES 
    ('00000000-0000-0000-0000-000000000000', 'Default Tenant', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 创建 id 为 'default' 的租户（如果不存在）
INSERT INTO tenants (id, name, status, created_at, updated_at)
VALUES ('default', 'Default Tenant (String)', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 插入菜单数据（幂等性：如果菜单已存在则更新，不存在则插入）
-- 注意：
--   - type 字段使用小写格式（menu, menu_dir, button），与前端期望一致
--   - render_type 字段标识渲染方式：tab、iframe、link
--   - component 字段需要使用 /src/views/backend/ 前缀的完整路径
--   - tenant_id 使用 'default'（与 Auth 服务生成的 token 中的 tenantId 一致）
INSERT INTO menus (id, tenant_id, parent_id, name, title, path, icon, type, render_type, component, url, keepalive, display_order, visible, config, extend, created_at, updated_at)
VALUES 
    -- ========== 根菜单（tenant_id = 'default'）==========
    -- 仪表盘（来自 V6）
    ('11111111-1111-1111-1111-111111111111', 'default', NULL, 'Dashboard', '仪表盘', '/admin/dashboard/index', 'fa fa-dashboard', 'menu', 'tab', '/src/views/backend/dashboard.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户管理目录（来自 V6）
    ('22222222-2222-2222-2222-222222222222', 'default', NULL, 'TenantManagement', '租户管理', '/admin/tenant', 'fa fa-building', 'menu_dir', 'tab', NULL, NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 用户管理目录（来自 V6）
    ('55555555-5555-5555-5555-555555555555', 'default', NULL, 'UserManagement', '用户管理', '/admin/user', 'fa fa-users', 'menu_dir', 'tab', NULL, NULL, false, 3, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 菜单管理（来自 V7）
    ('88888888-8888-8888-8888-888888888888', 'default', NULL, 'MenuManagement', '菜单规则管理', '/admin/menu', 'fa fa-list-alt', 'menu', 'tab', '/src/views/backend/menu/index.vue', NULL, false, 4, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 设置（新增，参考 Workbench）
    ('99999999-9999-9999-9999-999999999999', 'default', NULL, 'Settings', '设置', '/admin/settings', 'fa fa-gear', 'menu', 'tab', '/src/views/backend/login.vue', NULL, false, 5, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- ========== 租户管理子菜单（来自 V6，tenant_id = 'default'）==========
    -- 租户列表
    ('33333333-3333-3333-3333-333333333333', 'default', '22222222-2222-2222-2222-222222222222', 'TenantList', '租户列表', '/admin/tenant/tenant/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/tenant/tenant/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 租户组管理
    ('44444444-4444-4444-4444-444444444444', 'default', '22222222-2222-2222-2222-222222222222', 'TenantGroupList', '租户组管理', '/admin/tenant/group/index', 'fa fa-sitemap', 'menu', 'tab', '/src/views/backend/tenant/group/index.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- ========== 用户管理子菜单（来自 V6，tenant_id = 'default'）==========
    -- 用户列表
    ('66666666-6666-6666-6666-666666666666', 'default', '55555555-5555-5555-5555-555555555555', 'UserList', '用户列表', '/admin/user/user/index', 'fa fa-list', 'menu', 'tab', '/src/views/backend/user/user/index.vue', NULL, false, 1, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    -- 用户组管理
    ('77777777-7777-7777-7777-777777777777', 'default', '55555555-5555-5555-5555-555555555555', 'UserGroupList', '用户组管理', '/admin/user/group/index', 'fa fa-sitemap', 'menu', 'tab', '/src/views/backend/user/group/index.vue', NULL, false, 2, true, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
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

