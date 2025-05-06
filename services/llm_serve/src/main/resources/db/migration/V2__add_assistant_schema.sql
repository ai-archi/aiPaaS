-- Set search path to ai_xone schema
SET search_path TO ai_xone;

-- Assistants Table
CREATE TABLE assistants (
    id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    model_id VARCHAR(255) NOT NULL,
    capability JSONB NOT NULL,
    tools JSONB,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    CONSTRAINT fk_assistants_model FOREIGN KEY (model_id) REFERENCES model_configs(model_id)
);

-- Create index on model_id for faster lookups
CREATE INDEX idx_assistants_model_id ON assistants (model_id);

-- Create index on active status
CREATE INDEX idx_assistants_active ON assistants (active);

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
    CONSTRAINT fk_threads_assistant FOREIGN KEY (assistant_id) REFERENCES assistants(id)
);

-- Create index on assistant_id for faster lookups
CREATE INDEX idx_threads_assistant_id ON threads (assistant_id);

-- Create index on user_id for faster lookups
CREATE INDEX idx_threads_user_id ON threads (user_id);

-- Create index on status for filtering
CREATE INDEX idx_threads_status ON threads (status);

-- Messages Table
CREATE TABLE messages (
    id VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL,
    thread_id VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    tool_calls JSONB,
    metadata JSONB,
    created_at BIGINT NOT NULL,
    CONSTRAINT fk_messages_thread FOREIGN KEY (thread_id) REFERENCES threads(id)
);

-- Create index on thread_id for faster lookups
CREATE INDEX idx_messages_thread_id ON messages (thread_id);

-- Create index on role for filtering
CREATE INDEX idx_messages_role ON messages (role);

-- Create index on created_at for time-based queries
CREATE INDEX idx_messages_created_at ON messages (created_at); 