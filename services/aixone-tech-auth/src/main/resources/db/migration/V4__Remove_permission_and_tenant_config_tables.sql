-- 移除权限相关的表（权限管理已迁移到Directory服务）
DROP TABLE IF EXISTS abac_policy_attributes CASCADE;
DROP TABLE IF EXISTS abac_policies CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;

-- 移除租户配置表（Auth服务只做多租户适配，不管理租户配置）
DROP TABLE IF EXISTS tenant_configs CASCADE;

