# aixone-event-center Kafka 集成指南

## 概述

aixone-event-center 现已支持 Kafka 作为消息中间件，提供高性能的事件发布和订阅能力。

## 配置说明

### 1. Kafka 服务器配置

项目已配置连接到本地 Kafka 服务器：
- **Bootstrap Servers**: `localhost:9092`
- **协议**: PLAINTEXT
- **配置来源**: `/usr/local/etc/kafka/server.properties`

### 2. 应用配置

在 `application.yml` 中已添加完整的 Kafka 配置：

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
    consumer:
      group-id: event-center-group
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      auto-offset-reset: earliest
```

## 核心功能

### 1. Topic 管理

#### 注册 Topic
```bash
POST /api/topics/register
Content-Type: application/json

{
  "name": "user-events",
  "owner": "user-service",
  "desc": "用户相关事件"
}
```

#### 查询所有 Topic
```bash
GET /api/topics
```

#### 删除 Topic
```bash
DELETE /api/topics/{topicName}
```

### 2. 事件发布

#### 发布事件到 Kafka
```bash
POST /api/events/kafka/{topicName}
Content-Type: application/json

{
  "eventType": "user.login",
  "source": "user-service",
  "data": {
    "userId": "12345",
    "loginTime": "2024-01-01T10:00:00Z"
  }
}
```

#### 仅持久化到数据库
```bash
POST /api/events
Content-Type: application/json

{
  "eventType": "user.login",
  "source": "user-service",
  "data": {
    "userId": "12345",
    "loginTime": "2024-01-01T10:00:00Z"
  }
}
```

## 架构设计

### 1. 分离式架构
- **事件中心**: 负责 Topic 治理、事件审计、监控
- **Kafka**: 负责高性能事件流转
- **业务服务**: 通过 SDK 直接对接 Kafka

### 2. 合规控制
- 所有 Topic 必须通过事件中心注册
- 未注册的 Topic 无法发布事件
- 支持 Topic 生命周期管理

### 3. 多租户支持
- 所有操作支持租户隔离
- Topic 和事件按租户维度管理

## 开发指南

### 1. 启动 Kafka

确保 Kafka 服务器正在运行：
```bash
# 启动 Kafka (macOS with Homebrew)
brew services start kafka
```

### 2. 运行应用

```bash
mvn spring-boot:run
```

### 3. 测试连接

运行集成测试：
```bash
mvn test -Dtest=KafkaIntegrationTest
```

## 监控和运维

### 1. 健康检查
- 访问 `/actuator/health` 查看 Kafka 连接状态
- 访问 `/actuator/metrics` 查看 Kafka 相关指标

### 2. 日志监控
- 所有 Kafka 操作都有详细日志
- 支持结构化日志输出

### 3. 错误处理
- 自动重试机制
- 详细的错误信息
- 监控指标统计

## 扩展功能

### 1. 事件消费
未来可添加事件消费服务，从 Kafka 订阅事件进行处理。

### 2. 多消息中间件支持
架构设计支持多种消息中间件（Kafka、RocketMQ 等）。

### 3. 高级特性
- 事件分区策略
- 消息压缩
- 批量处理
- 死信队列

## 故障排除

### 1. 连接问题
- 检查 Kafka 服务器是否运行
- 验证端口 9092 是否可访问
- 查看应用日志中的连接错误

### 2. Topic 创建失败
- 检查 Topic 名称是否符合规范
- 验证是否有足够的权限
- 查看 Kafka 服务器日志

### 3. 消息发送失败
- 检查 Topic 是否已注册
- 验证消息格式是否正确
- 查看生产者配置

## 性能优化

### 1. 生产者配置
- 调整 `batch.size` 和 `linger.ms` 提高吞吐量
- 配置适当的 `retries` 和 `acks` 保证可靠性

### 2. 消费者配置
- 调整 `max.poll.records` 控制批处理大小
- 配置适当的 `session.timeout.ms` 和 `heartbeat.interval.ms`

### 3. 网络优化
- 调整 `socket.send.buffer.bytes` 和 `socket.receive.buffer.bytes`
- 配置适当的 `request.timeout.ms`
