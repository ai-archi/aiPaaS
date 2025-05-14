-- Set search path to ai_xone schema
SET search_path TO ai_xone;

-- Model Configs Table
CREATE TABLE model_configs (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(1024) NOT NULL,
    max_tokens INTEGER NOT NULL,
    active BOOLEAN,
    is_system_preset BOOLEAN,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    min_input_price NUMERIC(18,6) NOT NULL,
    min_output_price NUMERIC(18,6) NOT NULL,
    charge_type VARCHAR(32),
    support_text_generation BOOLEAN,
    support_image_generation BOOLEAN,
    support_speech_generation BOOLEAN,
    support_video_generation BOOLEAN,
    support_vector BOOLEAN,
    description TEXT,
    tenant_id VARCHAR(255),
    provider_name VARCHAR(255),
    price_unit VARCHAR(32),
    currency VARCHAR(32),
    qps_limit INTEGER,
    region VARCHAR(64),
    tags TEXT,
    status VARCHAR(32)
);

-- Create index on active status
CREATE INDEX idx_model_configs_active ON model_configs (active);

-- Create index on tenant_id
CREATE INDEX idx_model_configs_tenant_id ON model_configs (tenant_id);

-- User Model Keys Table
CREATE TABLE user_model_keys (
    id VARCHAR(255) PRIMARY KEY,
    owner_id VARCHAR(255) NOT NULL,
    model_name VARCHAR(255) NOT NULL,
    api_key VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    description TEXT
);
CREATE INDEX idx_user_model_keys_owner_model ON user_model_keys (owner_id, model_name);

-- User Model Key Grants Table
CREATE TABLE user_model_key_grants (
    id VARCHAR(255) PRIMARY KEY,
    key_id VARCHAR(255) NOT NULL,
    grantee_id VARCHAR(255) NOT NULL,
    charge_type VARCHAR(32),
    price NUMERIC(18,6),
    price_unit VARCHAR(32),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    description TEXT,
    CONSTRAINT fk_key FOREIGN KEY (key_id) REFERENCES user_model_keys(id)
);
CREATE INDEX idx_user_model_key_grants_grantee ON user_model_key_grants (grantee_id);
CREATE INDEX idx_user_model_key_grants_keyid ON user_model_key_grants (key_id);

-- 预置数据
INSERT INTO model_configs (id, name, endpoint, max_tokens, active, is_system_preset, version, created_at, updated_at, min_input_price, min_output_price, charge_type, support_text_generation, support_image_generation, support_speech_generation, support_video_generation, support_vector, description, tenant_id, provider_name, price_unit, currency, qps_limit, region, tags, status)
VALUES
('50c2dc76-3f37-415e-b734-7d086f7ade59', 'deepseek-chat', 'https://api.deepseek.com/v1', 4096, true, false, 0, '2025-05-09 11:10:17.667368', '2025-05-09 11:10:17.667388', 0.010000, 0.020000, 'token', true, false, false, false, false, '自定义文本生成模型', 'public', 'deepseek', 'CNY/1k tokens', 'CNY', 10, 'cn-shanghai', 'NLP,text', 'active'),
('45f626c7-c33a-4030-a57f-f076bfc94b9c', 'wanx2.1-imageedit', 'https://dashscope.aliyuncs.com/api/v1', 4096, true, false, 0, '2025-05-13 14:01:41.224704', '2025-05-13 14:01:41.224714', 0.140000, 0.140000, 'token', false, true, false, false, false, '通用图像编辑', 'public', 'tongyi', 'CNY/1k tokens', 'CNY', 10, 'cn-shanghai', 'Image', 'active'),
('4c952290-ab52-4eb7-be6f-5c9373748e32', 'wanx-style-repaint-v1', 'https://dashscope.aliyuncs.com/api/v1', 4096, true, false, 0, '2025-05-13 18:53:13.450901', '2025-05-13 18:53:13.450916', 0.010000, 0.020000, 'token', true, false, false, false, false, '通用图像编辑', 'public', 'tongyi', 'CNY/1k tokens', 'CNY', 10, 'cn-shanghai', 'Image', 'active'),
('4c952290-ab52-4eb7-be6f-5c9373748e35', 'wanx2.0-t2i-turbo', 'https://dashscope.aliyuncs.com/api/v1', 4096, true, false, 0, '2025-05-13 18:53:13.450901', '2025-05-13 18:53:13.450916', 0.010000, 0.020000, 'token', true, false, false, false, false, '文生图', 'public', 'tongyi', 'CNY/1k tokens', 'CNY', 10, 'cn-shanghai', 'Image', 'active');

-- 系统默认API Key（owner_id = 'system'）
INSERT INTO user_model_keys (id, owner_id, model_name, api_key, created_at, updated_at, description) VALUES
('syskey-1', 'system', 'deepseek-chat', 'sk-b3c80d381eb54480a4e3e1b9de1c2a51', now(), now(), '系统默认key'),
('syskey-2', 'system', 'wanx2.1-imageedit', 'sk-80c3d43f769648a89ca479e89228495b', now(), now(), '系统默认key'),
('syskey-3', 'system', 'wanx-style-repaint-v1', 'sk-80c3d43f769648a89ca479e89228495b', now(), now(), '系统默认key'),
('syskey-4', 'system', 'wanx2.0-t2i-turbo', 'sk-80c3d43f769648a89ca479e89228495b', now(), now(), '系统默认key');

-- 示例授权数据（假设用户userA被授权使用deepseek-chat的key，按次计费）
INSERT INTO user_model_key_grants (id, key_id, grantee_id, charge_type, price, price_unit, created_at, updated_at, description) VALUES
('grant-1', 'syskey-1', 'userA', 'count', 0.05, 'CNY/次', now(), now(), 'userA被授权使用系统deepseek-chat key，按次计费');

-- User Quotas Table
CREATE TABLE user_quotas (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    quota_type VARCHAR(50) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    tenant_id VARCHAR(255),
    CONSTRAINT uk_user_model_quota UNIQUE (user_id, model_id, quota_type),
    CONSTRAINT fk_user_quotas_model FOREIGN KEY (model_id) REFERENCES model_configs(id)
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_user_quotas_user_id ON user_quotas (user_id);

-- Create index on model_id for faster lookups
CREATE INDEX idx_user_quotas_model_id ON user_quotas (model_id);

-- Create index on tenant_id
CREATE INDEX idx_user_quotas_tenant_id ON user_quotas (tenant_id);

-- Quota Transactions Table
CREATE TABLE quota_transactions (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    quota_type VARCHAR(50) NOT NULL,
    amount BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_at BIGINT NOT NULL,
    tenant_id VARCHAR(255),
    CONSTRAINT fk_quota_transactions_model FOREIGN KEY (model_id) REFERENCES model_configs(id)
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_quota_transactions_user_id ON quota_transactions (user_id);

-- Create index on model_id for faster lookups
CREATE INDEX idx_quota_transactions_model_id ON quota_transactions (model_id);

-- Create index on created_at for time-based queries
CREATE INDEX idx_quota_transactions_created_at ON quota_transactions (created_at);

-- Create index on tenant_id
CREATE INDEX idx_quota_transactions_tenant_id ON quota_transactions (tenant_id);

-- Assistants Table
CREATE TABLE assistants (
    id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    model_id VARCHAR(255) NOT NULL,
    capability TEXT NOT NULL,
    tools TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    tenant_id VARCHAR(255),
    CONSTRAINT fk_assistants_model FOREIGN KEY (model_id) REFERENCES model_configs(id)
);

-- Create index on model_id
CREATE INDEX idx_assistants_model_id ON assistants (model_id);

-- Create index on active status
CREATE INDEX idx_assistants_active ON assistants (active);

-- Create index on tenant_id
CREATE INDEX idx_assistants_tenant_id ON assistants (tenant_id);

-- Threads Table
CREATE TABLE threads (
    id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    assistant_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    tenant_id VARCHAR(255),
    CONSTRAINT fk_threads_assistant FOREIGN KEY (assistant_id) REFERENCES assistants(id)
);

-- Create index on assistant_id
CREATE INDEX idx_threads_assistant_id ON threads (assistant_id);

-- Create index on user_id
CREATE INDEX idx_threads_user_id ON threads (user_id);

-- Create index on status
CREATE INDEX idx_threads_status ON threads (status);

-- Create index on tenant_id
CREATE INDEX idx_threads_tenant_id ON threads (tenant_id);

-- Messages Table
CREATE TABLE messages (
    id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    thread_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    tool_calls TEXT,
    metadata TEXT,
    created_at BIGINT NOT NULL,
    tenant_id VARCHAR(255),
    CONSTRAINT fk_messages_thread FOREIGN KEY (thread_id) REFERENCES threads(id)
);

-- Create index on thread_id
CREATE INDEX idx_messages_thread_id ON messages (thread_id);

-- Create index on role
CREATE INDEX idx_messages_role ON messages (role);

-- Create index on created_at
CREATE INDEX idx_messages_created_at ON messages (created_at);

-- Create index on tenant_id
CREATE INDEX idx_messages_tenant_id ON messages (tenant_id);

-- CREATE INDEX idx_model_configs_provider_id ON model_configs ((provider_info_json->>'providerId')); 