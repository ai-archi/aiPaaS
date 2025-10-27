-- 更新验证码表结构，将id字段改为VARCHAR类型以支持UUID

-- 首先删除现有的验证码表（因为数据不重要，可以直接重建）
DROP TABLE IF EXISTS verification_codes;

-- 重新创建验证码表，使用VARCHAR类型的id字段
CREATE TABLE verification_codes (
    id VARCHAR(36) PRIMARY KEY,
    phone VARCHAR(20),
    email VARCHAR(255),
    tenant_id VARCHAR(255) NOT NULL,
    code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type VARCHAR(20) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_verification_codes_tenant_id ON verification_codes(tenant_id);
CREATE INDEX IF NOT EXISTS idx_verification_codes_phone ON verification_codes(phone);
CREATE INDEX IF NOT EXISTS idx_verification_codes_email ON verification_codes(email);
CREATE INDEX IF NOT EXISTS idx_verification_codes_expires_at ON verification_codes(expires_at);
CREATE INDEX IF NOT EXISTS idx_verification_codes_created_at ON verification_codes(created_at);
