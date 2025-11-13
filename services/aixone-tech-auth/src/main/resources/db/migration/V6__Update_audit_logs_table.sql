-- 更新 audit_logs 表以匹配 AuditLogEntity 实体
-- 添加缺失的列

-- 如果 log_id 列存在，先删除（因为实体中没有这个字段）
DO $$ 
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'log_id'
    ) THEN
        ALTER TABLE audit_logs DROP COLUMN log_id;
    END IF;
END $$;

-- 添加缺失的列（如果不存在）
DO $$ 
BEGIN
    -- 添加 details 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'details'
    ) THEN
        ALTER TABLE audit_logs ADD COLUMN details TEXT;
    END IF;
    
    -- 添加 error_message 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'error_message'
    ) THEN
        ALTER TABLE audit_logs ADD COLUMN error_message TEXT;
    END IF;
    
    -- 添加 session_id 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'session_id'
    ) THEN
        ALTER TABLE audit_logs ADD COLUMN session_id VARCHAR(100);
    END IF;
    
    -- 添加 created_time 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'created_time'
    ) THEN
        ALTER TABLE audit_logs ADD COLUMN created_time TIMESTAMP;
        -- 为现有记录设置默认值
        UPDATE audit_logs SET created_time = timestamp WHERE created_time IS NULL;
    END IF;
    
    -- 添加 updated_time 列
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'audit_logs' AND column_name = 'updated_time'
    ) THEN
        ALTER TABLE audit_logs ADD COLUMN updated_time TIMESTAMP;
        -- 为现有记录设置默认值
        UPDATE audit_logs SET updated_time = timestamp WHERE updated_time IS NULL;
    END IF;
END $$;


