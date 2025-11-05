-- 为用户表添加phone字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone VARCHAR(20);

-- 为phone字段创建唯一索引（phone + tenant_id）
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_phone_tenant ON users(phone, tenant_id) WHERE phone IS NOT NULL;

