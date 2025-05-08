-- Set search path to ai_xone schema
SET search_path TO ai_xone;

-- Model Configs Table
CREATE TABLE model_configs (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    endpoint VARCHAR(1024) NOT NULL,
    api_key VARCHAR(1024) NOT NULL,
    max_tokens INTEGER NOT NULL,
    active BOOLEAN,
    is_system_preset BOOLEAN,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    min_input_price NUMERIC(18,6) NOT NULL,
    min_output_price NUMERIC(18,6) NOT NULL,
    support_text_generation BOOLEAN,
    support_image_generation BOOLEAN,
    support_speech_generation BOOLEAN,
    support_video_generation BOOLEAN,
    support_vector BOOLEAN,
    description TEXT,
    tenant_id VARCHAR(255),
    provider_name VARCHAR(255),
    model_code VARCHAR(255),
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