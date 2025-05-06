-- Set search path to ai_xone schema
SET search_path TO ai_xone;

-- Model Configs Table
CREATE TABLE model_configs (
    model_id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    provider_info JSONB NOT NULL,
    capability JSONB NOT NULL,
    runtime_config JSONB NOT NULL,
    billing_rule JSONB NOT NULL,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Create index on provider_id for faster lookups
CREATE INDEX idx_model_configs_provider_id ON model_configs ((provider_info->>'providerId'));

-- Create index on active status
CREATE INDEX idx_model_configs_active ON model_configs (active);

-- User Quotas Table
CREATE TABLE user_quotas (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    quota_type VARCHAR(50) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT uk_user_model_quota UNIQUE (user_id, model_id, quota_type),
    CONSTRAINT fk_user_quotas_model FOREIGN KEY (model_id) REFERENCES model_configs(model_id)
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_user_quotas_user_id ON user_quotas (user_id);

-- Create index on model_id for faster lookups
CREATE INDEX idx_user_quotas_model_id ON user_quotas (model_id);

-- Quota Transactions Table
CREATE TABLE quota_transactions (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    quota_type VARCHAR(50) NOT NULL,
    amount BIGINT NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    created_at BIGINT NOT NULL,
    CONSTRAINT fk_quota_transactions_model FOREIGN KEY (model_id) REFERENCES model_configs(model_id)
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_quota_transactions_user_id ON quota_transactions (user_id);

-- Create index on model_id for faster lookups
CREATE INDEX idx_quota_transactions_model_id ON quota_transactions (model_id);

-- Create index on created_at for time-based queries
CREATE INDEX idx_quota_transactions_created_at ON quota_transactions (created_at); 