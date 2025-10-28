-- 添加 reason 字段到 token_blacklist 表
ALTER TABLE token_blacklist ADD COLUMN IF NOT EXISTS reason VARCHAR(100) NOT NULL DEFAULT 'LOGOUT';

