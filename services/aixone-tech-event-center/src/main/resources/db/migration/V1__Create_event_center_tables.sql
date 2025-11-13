-- AixOne事件中心数据库迁移脚本 V1
-- 创建所有核心表结构

-- 1. 事件表
CREATE TABLE IF NOT EXISTS events (
    event_id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    event_source VARCHAR(100) NOT NULL,
    event_data JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tenant_id VARCHAR(50),
    correlation_id VARCHAR(100),
    version INTEGER NOT NULL DEFAULT 1
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_events_tenant_id ON events(tenant_id);
CREATE INDEX IF NOT EXISTS idx_events_event_type ON events(event_type);
CREATE INDEX IF NOT EXISTS idx_events_created_at ON events(created_at);
CREATE INDEX IF NOT EXISTS idx_events_correlation_id ON events(correlation_id);
CREATE INDEX IF NOT EXISTS idx_events_status ON events(status);

-- 2. 订阅表
CREATE TABLE IF NOT EXISTS subscriptions (
    subscription_id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    subscriber_service VARCHAR(200) NOT NULL,
    subscriber_endpoint VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    filter_config JSONB,
    retry_config JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_subscriptions_tenant_id ON subscriptions(tenant_id);
CREATE INDEX IF NOT EXISTS idx_subscriptions_event_type ON subscriptions(event_type);
CREATE INDEX IF NOT EXISTS idx_subscriptions_status ON subscriptions(status);

-- 3. 事件分发记录表
CREATE TABLE IF NOT EXISTS event_delivery_records (
    record_id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    subscription_id BIGINT NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    max_retries INTEGER NOT NULL DEFAULT 3,
    next_retry_at TIMESTAMP,
    delivered_at TIMESTAMP,
    error_message VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_delivery_record_event FOREIGN KEY (event_id) REFERENCES events(event_id) ON DELETE CASCADE,
    CONSTRAINT fk_delivery_record_subscription FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_delivery_records_event_id ON event_delivery_records(event_id);
CREATE INDEX IF NOT EXISTS idx_delivery_records_subscription_id ON event_delivery_records(subscription_id);
CREATE INDEX IF NOT EXISTS idx_delivery_records_tenant_id ON event_delivery_records(tenant_id);
CREATE INDEX IF NOT EXISTS idx_delivery_records_status ON event_delivery_records(status);
CREATE INDEX IF NOT EXISTS idx_delivery_records_next_retry_at ON event_delivery_records(next_retry_at) WHERE status = 'RETRYING';

-- 4. Topic表
CREATE TABLE IF NOT EXISTS topics (
    topic_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    owner VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL,
    tenant_id VARCHAR(50),
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    partition_count INTEGER DEFAULT 1,
    replication_factor SMALLINT DEFAULT 1
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_topics_tenant_id ON topics(tenant_id);
CREATE INDEX IF NOT EXISTS idx_topics_status ON topics(status);

-- 5. 通知表
CREATE TABLE IF NOT EXISTS notifications (
    notification_id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    recipient_info JSONB NOT NULL,
    notification_content JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    channel VARCHAR(20) NOT NULL,
    template_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP,
    error_message VARCHAR(1000)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_notifications_tenant_id ON notifications(tenant_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_notification_type ON notifications(notification_type);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);
CREATE INDEX IF NOT EXISTS idx_notifications_channel ON notifications(channel);

-- 6. 通知模板表
CREATE TABLE IF NOT EXISTS notification_templates (
    template_id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    template_name VARCHAR(100) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    subject_template VARCHAR(500),
    body_template TEXT NOT NULL,
    channels VARCHAR(200),
    variables JSONB,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(tenant_id, template_name)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_templates_tenant_id ON notification_templates(tenant_id);
CREATE INDEX IF NOT EXISTS idx_templates_template_name ON notification_templates(template_name);
CREATE INDEX IF NOT EXISTS idx_templates_notification_type ON notification_templates(notification_type);

-- 7. 调度任务表
CREATE TABLE IF NOT EXISTS schedule_tasks (
    task_id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    task_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    task_type VARCHAR(50) NOT NULL,
    schedule_expression VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    executor_service VARCHAR(100) NOT NULL,
    task_params TEXT,
    max_retry_count INTEGER DEFAULT 3,
    current_retry_count INTEGER DEFAULT 0,
    last_execute_time TIMESTAMP,
    next_execute_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(tenant_id, task_name)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_tasks_tenant_id ON schedule_tasks(tenant_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON schedule_tasks(status);
CREATE INDEX IF NOT EXISTS idx_tasks_next_execute_time ON schedule_tasks(next_execute_time) WHERE status = 'PENDING';
CREATE INDEX IF NOT EXISTS idx_tasks_task_type ON schedule_tasks(task_type);

-- 8. 任务日志表
CREATE TABLE IF NOT EXISTS task_logs (
    log_id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    execute_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    result_message TEXT,
    error_message TEXT,
    error_stack TEXT,
    duration_ms BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_log_task FOREIGN KEY (task_id) REFERENCES schedule_tasks(task_id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_task_logs_task_id ON task_logs(task_id);
CREATE INDEX IF NOT EXISTS idx_task_logs_execute_time ON task_logs(execute_time);
CREATE INDEX IF NOT EXISTS idx_task_logs_status ON task_logs(status);

-- 添加注释
COMMENT ON TABLE events IS '事件表，存储系统中发生的重要业务事件';
COMMENT ON TABLE subscriptions IS '订阅表，存储对特定事件类型的订阅配置';
COMMENT ON TABLE event_delivery_records IS '事件分发记录表，记录事件分发到订阅者的历史，用于重试和追踪';
COMMENT ON TABLE topics IS 'Topic表，存储Kafka Topic的元数据和管理信息';
COMMENT ON TABLE notifications IS '通知表，存储系统中发送的通知消息';
COMMENT ON TABLE notification_templates IS '通知模板表，存储可重用的通知模板';
COMMENT ON TABLE schedule_tasks IS '调度任务表，存储任务元数据和调度策略';
COMMENT ON TABLE task_logs IS '任务日志表，记录任务执行历史';

