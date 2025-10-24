# AixOne事件中心架构设计

## 术语表

| 名称         | 说明                                                         |
|--------------|--------------------------------------------------------------|
| 事件         | 业务或系统中发生的可观测动作（如用户操作、系统状态变更等）     |
| 订阅         | 业务服务/模块声明对某类事件感兴趣，需被通知                   |
| 发布         | 事件源将事件推送到事件中心                                   |
| 通知         | 面向最终用户的消息推送（邮件、短信、推送等）                 |
| 模板         | 可配置的消息模板，支持变量替换和格式化                       |
| 调度任务     | 平台内定时/周期/分布式执行的任务                             |
| 调度节点     | 参与任务调度的服务实例                                       |
| 租户         | 平台的逻辑隔离单元，支持多租户架构                           |

## 一、服务概述

### 1.1 服务定位

AixOne事件中心是技术平台的核心基础设施服务，提供统一的事件治理、通知管理和任务调度能力。作为平台级的事件基础设施，为整个AixOne生态系统提供标准化的事件发布、订阅、通知和调度支撑。

### 1.2 核心价值

- **统一事件治理**：提供统一的事件发布、订阅、路由和存储能力
- **多渠道通知**：支持邮件、短信、推送等多种通知方式
- **任务调度**：提供分布式任务调度和定时任务管理
- **高可用保障**：支持高并发、高可用的事件处理
- **可扩展性**：支持水平扩展和功能扩展

### 1.3 设计原则

- **DDD分层架构**：采用领域驱动设计，便于后期微服务拆分
- **模块化设计**：事件模块和通知模块相对独立，便于独立演进
- **服务化接口**：提供统一的API接口和SDK
- **高内聚低耦合**：模块内部高内聚，模块间低耦合
- **可观测性**：提供完善的监控、日志和链路追踪能力

## 二、技术架构

### 2.1 技术选型

- **语言与框架**：Java 21+，Spring Boot 3.5+，Spring Cloud 2023.x
- **消息队列**：Apache Kafka（事件分发）+ Redis Pub/Sub（通知分发）
- **数据库**：PostgreSQL（主存储）+ Redis（缓存和会话）
- **任务调度**：Quartz + Redis（分布式协调）
- **依赖组件**：Spring Cloud Stream，Spring Cache，Micrometer

### 2.2 技术架构图

```mermaid
graph TB
    subgraph "AixOne事件中心"
        subgraph "接口层"
            A[REST API网关]
            B[事件SDK]
            C[通知SDK]
            D[调度SDK]
        end
        
        subgraph "应用层"
            E[事件应用服务]
            F[通知应用服务]
            G[调度应用服务]
        end
        
        subgraph "领域层"
            H[事件领域]
            I[通知领域]
            J[调度领域]
        end
        
        subgraph "基础设施层"
            K[消息队列]
            L[数据存储]
            M[外部服务]
            N[缓存]
        end
    end
    
    subgraph "外部微服务"
        O[业务服务A]
        P[业务服务B]
        Q[业务服务C]
    end
    
    A --> E
    B --> E
    C --> F
    D --> G
    E --> H
    F --> I
    G --> J
    H --> K
    I --> M
    J --> L
    
    O --> B
    P --> C
    Q --> D
```

### 2.3 SDK设计概述

| SDK名称 | 核心功能 | 集成方式 | 主要特性 |
|---------|----------|----------|----------|
| **aixone-event-sdk** | 事件治理、上下文管理、监控 | Maven依赖 + 注解 | 统一协议、链路追踪、事件注册、Schema验证 |
| **aixone-notification-sdk** | 通知治理、多渠道管理、模板引擎 | Maven依赖 + 注解 | 统一协议、上下文传递、模板管理、发送策略 |
| **aixone-schedule-sdk** | 任务调度治理、分布式执行、监控 | Maven依赖 + 注解 | 统一协议、任务注册、资源管理、故障转移 |

### 2.4 模块划分

| 模块名称 | 核心职责 | 主要功能 | 技术特点 |
|---------|----------|----------|----------|
| **事件模块** | 事件治理和管理 | 事件发布、订阅、路由、存储、检索 | 高性能、低延迟 |
| **通知模块** | 多渠道通知 | 邮件、短信、推送、模板管理 | 多渠道、可配置 |
| **调度模块** | 任务调度 | 定时任务、分布式调度、监控 | 分布式、高可用 |

## 三、SDK设计

### 3.1 SDK设计理念

SDK不仅仅是消息中间件的封装，而是提供统一的事件治理、监控、上下文管理能力的平台级组件。通过标准化的协议、数据结构和治理机制，实现跨服务的统一事件管理。

#### 3.1.1 核心设计原则
- **统一协议**：定义标准的事件协议和数据格式
- **上下文管理**：支持链路追踪、租户隔离、权限传递
- **治理能力**：提供事件注册、版本管理、兼容性检查
- **监控集成**：内置监控指标、链路追踪、异常告警
- **可观测性**：提供完整的可观测性能力

### 3.2 aixone-event-sdk

#### 3.2.1 SDK定位
aixone-event-sdk为所有业务服务提供统一的事件治理能力，不仅仅是Kafka封装，而是提供完整的事件生命周期管理、治理、监控和上下文传递能力。

#### 3.2.2 核心能力

##### 3.2.2.1 统一事件协议
- **标准事件格式**：定义统一的事件数据结构
- **事件元数据**：包含事件版本、来源、时间戳、租户信息等
- **事件分类**：支持事件类型注册和分类管理
- **版本兼容**：支持事件版本管理和向后兼容

##### 3.2.2.2 上下文管理
- **链路追踪**：自动注入TraceId、SpanId，支持分布式链路追踪
- **租户上下文**：自动传递租户ID，支持多租户隔离
- **用户上下文**：传递用户ID、角色、权限等上下文信息
- **业务上下文**：支持自定义业务上下文传递

##### 3.2.2.3 事件治理
- **事件注册**：支持事件类型注册和Schema验证
- **事件验证**：基于Schema的事件数据验证
- **事件路由**：智能事件路由和分发策略
- **事件转换**：支持事件格式转换和适配

##### 3.2.2.4 监控与可观测性
- **指标收集**：自动收集事件发布、订阅、处理指标
- **链路追踪**：完整的分布式链路追踪能力
- **异常监控**：异常事件自动捕获和告警
- **性能监控**：事件处理性能监控和分析

#### 3.2.3 使用方式

##### 3.2.3.1 事件发布
```java
@EventPublisher
public class OrderService {
    
    @PublishEvent(eventType = "order.created", version = "1.0")
    public void createOrder(Order order) {
        // 业务逻辑
        EventContext.publish("order.created", order, 
            EventMetadata.builder()
                .tenantId(getCurrentTenantId())
                .userId(getCurrentUserId())
                .businessContext(Map.of("orderId", order.getId()))
                .build());
    }
}
```

##### 3.2.3.2 事件订阅
```java
@EventSubscriber
public class NotificationService {
    
    @SubscribeEvent(eventType = "order.created", version = "1.0")
    public void handleOrderCreated(OrderCreatedEvent event, EventContext context) {
        // 自动获取上下文信息
        String tenantId = context.getTenantId();
        String userId = context.getUserId();
        Map<String, Object> businessContext = context.getBusinessContext();
        
        // 业务处理逻辑
    }
}
```

##### 3.2.3.3 配置管理
```yaml
aixone:
  event:
    enabled: true
    protocol:
      version: "1.0"
      schema-registry: "http://schema-registry:8081"
    context:
      tenant-header: "X-Tenant-Id"
      user-header: "X-User-Id"
      trace-header: "X-Trace-Id"
    monitoring:
      metrics-enabled: true
      tracing-enabled: true
      alert-enabled: true
    governance:
      event-registry: "http://event-center:8080/api/v1/events/registry"
      validation-enabled: true
```

### 3.3 aixone-notification-sdk

#### 3.3.1 SDK定位
aixone-notification-sdk提供统一的通知治理能力，支持多渠道通知、模板管理、发送策略、监控告警等完整功能。

#### 3.3.2 核心能力

##### 3.3.2.1 统一通知协议
- **标准通知格式**：定义统一的通知数据结构
- **多渠道适配**：支持邮件、短信、推送、IM等多种渠道
- **模板引擎**：支持变量替换、条件判断、循环等模板功能
- **发送策略**：支持优先级、限流、重试、批量发送等策略

##### 3.3.2.2 上下文管理
- **用户上下文**：自动获取用户偏好、联系方式等上下文
- **租户上下文**：支持租户级通知配置和策略
- **业务上下文**：支持业务相关的通知上下文传递
- **渠道上下文**：支持渠道特定的上下文管理

##### 3.3.2.3 通知治理
- **模板管理**：支持模板版本管理、A/B测试
- **渠道管理**：支持渠道配置、可用性检查
- **发送策略**：支持智能发送策略和降级机制
- **合规管理**：支持通知合规性检查和审计

##### 3.3.2.4 监控与可观测性
- **发送监控**：监控通知发送成功率、延迟等指标
- **渠道监控**：监控各渠道的可用性和性能
- **用户反馈**：收集用户反馈和满意度指标
- **异常告警**：异常情况自动告警和处理

#### 3.3.3 使用方式

##### 3.3.3.1 通知发送
```java
@NotificationSender
public class OrderService {
    
    public void sendOrderNotification(Order order) {
        NotificationContext.builder()
            .tenantId(getCurrentTenantId())
            .userId(order.getUserId())
            .templateId("order.created")
            .channel(NotificationChannel.EMAIL)
            .priority(NotificationPriority.HIGH)
            .context(Map.of("orderId", order.getId(), "amount", order.getAmount()))
            .build()
            .send();
    }
}
```

##### 3.3.3.2 模板管理
```java
@TemplateManager
public class NotificationTemplateService {
    
    @CreateTemplate(templateId = "order.created", version = "1.0")
    public void createOrderTemplate() {
        TemplateContext.builder()
            .templateId("order.created")
            .subject("订单创建通知")
            .content("您的订单{{orderId}}已创建，金额：{{amount}}")
            .channels(Arrays.asList(NotificationChannel.EMAIL, NotificationChannel.SMS))
            .build()
            .create();
    }
}
```

### 3.4 aixone-schedule-sdk

#### 3.4.1 SDK定位
aixone-schedule-sdk提供统一的任务调度治理能力，支持分布式调度、任务管理、监控告警等完整功能。

#### 3.4.2 核心能力

##### 3.4.2.1 统一调度协议
- **标准任务格式**：定义统一的任务数据结构
- **调度策略**：支持Cron、固定间隔、一次性等多种调度策略
- **任务类型**：支持HTTP回调、消息发送、数据处理等任务类型
- **执行策略**：支持任务分片、并行执行、故障转移等策略

##### 3.4.2.2 上下文管理
- **执行上下文**：支持任务执行时的上下文传递
- **租户上下文**：支持租户级任务隔离和配置
- **业务上下文**：支持业务相关的任务上下文
- **资源上下文**：支持资源限制和配额管理

##### 3.4.2.3 调度治理
- **任务注册**：支持任务注册、版本管理、兼容性检查
- **调度策略**：支持智能调度策略和负载均衡
- **故障处理**：支持任务故障检测、重试、转移
- **资源管理**：支持资源配额、限制、监控

##### 3.4.2.4 监控与可观测性
- **执行监控**：监控任务执行时间、成功率等指标
- **资源监控**：监控资源使用情况和性能指标
- **异常监控**：异常任务自动捕获和告警
- **性能分析**：任务性能分析和优化建议

#### 3.4.3 使用方式

##### 3.4.3.1 任务注册
```java
@TaskRegistry
public class OrderService {
    
    @RegisterTask(taskId = "order.cleanup", version = "1.0")
    public void registerOrderCleanupTask() {
        TaskContext.builder()
            .taskId("order.cleanup")
            .taskType(TaskType.HTTP_CALLBACK)
            .schedule("0 0 2 * * ?") // 每天凌晨2点执行
            .endpoint("http://order-service:8080/api/v1/orders/cleanup")
            .timeout(300000) // 5分钟超时
            .retryPolicy(RetryPolicy.builder().maxAttempts(3).build())
            .build()
            .register();
    }
}
```

##### 3.4.3.2 任务执行
```java
@TaskExecutor
public class OrderCleanupTask {
    
    @ExecuteTask(taskId = "order.cleanup")
    public void executeOrderCleanup(TaskContext context) {
        // 获取上下文信息
        String tenantId = context.getTenantId();
        Map<String, Object> businessContext = context.getBusinessContext();
        
        // 执行清理逻辑
        orderService.cleanupExpiredOrders(tenantId);
    }
}
```

### 3.5 SDK统一特性

#### 3.5.1 通用协议
- **统一数据格式**：所有SDK使用统一的数据结构和协议
- **版本管理**：支持API版本管理和向后兼容
- **错误处理**：统一的错误处理和异常管理
- **安全机制**：统一的认证、授权、加密机制

#### 3.5.2 上下文传递
- **链路追踪**：自动注入和传递TraceId、SpanId
- **租户隔离**：自动处理租户ID传递和隔离
- **用户上下文**：自动传递用户身份和权限信息
- **业务上下文**：支持自定义业务上下文传递

#### 3.5.3 治理能力
- **注册管理**：支持服务、事件、任务等资源的注册管理
- **版本控制**：支持资源版本管理和兼容性检查
- **配置管理**：支持动态配置管理和热更新
- **合规检查**：支持合规性检查和审计

#### 3.5.4 监控集成
- **指标收集**：自动收集关键业务和技术指标
- **链路追踪**：完整的分布式链路追踪能力
- **异常监控**：异常情况自动捕获和告警
- **性能分析**：性能数据收集和分析

## 四、核心功能

### 4.1 事件治理

#### 4.1.1 事件发布
- **多方式发布**：支持同步发布、异步发布、批量发布
- **事件验证**：事件类型注册验证、数据格式验证
- **事件路由**：基于事件类型的智能路由和分发
- **多租户支持**：支持租户级事件隔离和管理

#### 4.1.2 事件订阅
- **订阅管理**：支持动态订阅、取消订阅、订阅配置
- **过滤机制**：支持基于事件内容的条件过滤
- **重试策略**：支持可配置的重试机制和失败处理
- **多租户订阅**：支持租户级订阅隔离和管理

#### 4.1.3 事件存储
- **持久化存储**：事件数据持久化到PostgreSQL
- **索引优化**：基于租户和事件类型的复合索引
- **数据归档**：支持历史事件数据归档和清理
- **查询能力**：支持复杂条件的事件查询和检索

### 4.2 通知管理

#### 4.2.1 多渠道通知
- **邮件通知**：支持SMTP邮件发送，支持HTML和文本格式
- **短信通知**：集成阿里云SMS、腾讯云SMS等短信服务
- **推送通知**：支持极光推送、个推等移动推送服务
- **即时通讯**：支持微信、钉钉等即时通讯平台

#### 4.2.2 模板管理
- **模板引擎**：支持变量替换、条件判断、循环等模板功能
- **多格式支持**：支持HTML、文本、Markdown等多种格式
- **版本管理**：支持模板版本控制和回滚
- **多租户模板**：支持租户级模板隔离和管理

#### 4.2.3 通知调度
- **定时发送**：支持指定时间发送通知
- **批量发送**：支持批量通知发送和进度跟踪
- **发送策略**：支持优先级、限流、重试等发送策略
- **状态跟踪**：支持通知发送状态的实时跟踪

### 4.3 任务调度

#### 4.3.1 任务管理
- **任务注册**：支持动态任务注册、修改、删除
- **调度策略**：支持Cron表达式、固定间隔、一次性任务
- **任务类型**：支持HTTP回调、消息发送、数据处理等任务类型
- **多租户任务**：支持租户级任务隔离和管理

#### 4.3.2 分布式调度
- **节点管理**：支持调度节点的注册、心跳、故障转移
- **任务分片**：支持大任务的分片处理和并行执行
- **负载均衡**：支持任务在多个节点间的负载均衡
- **故障恢复**：支持节点故障时的任务自动迁移

#### 4.3.3 执行监控
- **执行日志**：记录任务执行的详细日志和结果
- **性能监控**：监控任务执行时间、成功率等指标
- **异常处理**：支持任务执行异常的重试和告警
- **审计追踪**：支持任务操作的完整审计追踪

## 五、数据架构

### 5.1 数据分类

| 数据类型 | 数据内容 | 存储方案 | 更新频率 | 安全等级 | 多租户隔离 |
|---------|----------|----------|----------|----------|------------|
| **事件数据** | 事件记录、事件历史 | PostgreSQL + Kafka | 实时更新 | 中 | 租户级隔离 |
| **通知数据** | 通知记录、模板数据 | PostgreSQL | 实时更新 | 中 | 租户级隔离 |
| **调度数据** | 任务配置、执行记录 | PostgreSQL | 实时更新 | 中 | 租户级隔离 |
| **缓存数据** | 临时数据、会话数据 | Redis | 实时更新 | 低 | 租户级隔离 |

### 5.2 数据安全

#### 5.2.1 数据加密
- **传输加密**：TLS 1.3
- **存储加密**：AES-256
- **密钥管理**：HSM + KMS
- **数据脱敏**：敏感数据脱敏处理

#### 5.2.2 访问控制
- **身份认证**：多因子认证
- **权限控制**：RBAC + ABAC
- **数据分类**：按敏感级别分类
- **审计日志**：完整的操作审计

### 5.3 实体模型

#### 5.3.1 Event（事件）
| 字段         | 类型    | 说明         | 必填 |
|--------------|---------|--------------|------|
| event_id     | String  | 事件ID       | 是   |
| tenant_id    | String  | 所属租户     | 是   |
| event_type   | String  | 事件类型     | 是   |
| event_source | String  | 事件源       | 是   |
| event_data   | JSONB   | 事件数据     | 是   |
| status       | String  | 事件状态     | 是   |
| created_at   | DateTime| 创建时间     | 是   |

#### 5.3.2 Subscription（订阅）
| 字段         | 类型    | 说明         | 必填 |
|--------------|---------|--------------|------|
| subscription_id | String | 订阅ID     | 是   |
| tenant_id    | String  | 所属租户     | 是   |
| event_type   | String  | 事件类型     | 是   |
| subscriber_service | String | 订阅服务 | 是   |
| subscriber_endpoint | String | 订阅端点 | 是   |
| status       | String  | 订阅状态     | 是   |
| created_at   | DateTime| 创建时间     | 是   |

#### 5.3.3 Notification（通知）
| 字段         | 类型    | 说明         | 必填 |
|--------------|---------|--------------|------|
| notification_id | String | 通知ID     | 是   |
| tenant_id    | String  | 所属租户     | 是   |
| notification_type | String | 通知类型 | 是   |
| recipient_info | JSONB | 接收者信息 | 是   |
| notification_content | JSONB | 通知内容 | 是   |
| status       | String  | 通知状态     | 是   |
| created_at   | DateTime| 创建时间     | 是   |

#### 5.3.4 ScheduledTask（调度任务）
| 字段         | 类型    | 说明         | 必填 |
|--------------|---------|--------------|------|
| task_id      | String  | 任务ID       | 是   |
| tenant_id    | String  | 所属租户     | 是   |
| task_name    | String  | 任务名称     | 是   |
| task_type    | String  | 任务类型     | 是   |
| schedule_config | JSONB | 调度配置 | 是   |
| task_payload | JSONB   | 任务载荷     | 否   |
| status       | String  | 任务状态     | 是   |
| created_at   | DateTime| 创建时间     | 是   |

### 5.4 关联关系
- 事件与订阅：一对多（按租户隔离）
- 通知与模板：多对一（按租户隔离）
- 任务与执行记录：一对多（按租户隔离）
- 租户与配置：一对一

## 六、API接口设计

### 6.1 事件接口

#### 6.1.1 事件发布
- **接口**：POST /api/v1/events
- **参数**：eventType, eventSource, eventData, eventMetadata
- **返回**：eventId, status

#### 6.1.2 事件订阅
- **接口**：POST /api/v1/events/subscriptions
- **参数**：eventType, subscriberService, subscriberEndpoint, subscriptionConfig
- **返回**：subscriptionId, status

#### 6.1.3 事件查询
- **接口**：GET /api/v1/events
- **参数**：tenantId, eventType, startTime, endTime, page, size
- **返回**：事件列表

### 6.2 通知接口

#### 6.2.1 发送通知
- **接口**：POST /api/v1/notifications
- **参数**：notificationType, recipientInfo, notificationContent, notificationConfig
- **返回**：notificationId, status

#### 6.2.2 模板管理
- **接口**：POST /api/v1/notifications/templates
- **参数**：templateName, notificationType, subjectTemplate, bodyTemplate, templateConfig
- **返回**：templateId, status

#### 6.2.3 通知查询
- **接口**：GET /api/v1/notifications
- **参数**：tenantId, notificationType, status, startTime, endTime, page, size
- **返回**：通知列表

### 6.3 调度接口

#### 6.3.1 创建任务
- **接口**：POST /api/v1/schedule/tasks
- **参数**：taskName, taskType, scheduleConfig, taskPayload
- **返回**：taskId, status

#### 6.3.2 任务管理
- **接口**：PUT /api/v1/schedule/tasks/{taskId}
- **参数**：taskName, scheduleConfig, taskPayload
- **返回**：status

#### 6.3.3 任务查询
- **接口**：GET /api/v1/schedule/tasks
- **参数**：tenantId, taskType, status, page, size
- **返回**：任务列表

### 6.4 多租户配置接口

#### 6.4.1 租户配置查询
- **接口**：GET /api/v1/tenant/{tenantId}/config
- **返回**：租户事件中心配置

#### 6.4.2 租户配置更新
- **接口**：PUT /api/v1/tenant/{tenantId}/config
- **参数**：eventConfig, notificationConfig, scheduleConfig
- **说明**：更新租户级事件中心配置

## 七、错误处理与安全

### 7.1 统一错误码设计

| code      | message           | 说明                     |
|-----------|-------------------|--------------------------|
| 0         | success           | 成功                     |
| 40001     | invalid_param     | 参数错误                 |
| 40101     | unauthorized      | 未认证/Token无效         |
| 40301     | forbidden         | 没有权限/注册关闭        |
| 40401     | not_found         | 资源不存在               |
| 50001     | internal_error    | 服务器内部错误           |

### 7.2 响应格式

所有接口返回格式：
```json
{ 
  "code": 0, 
  "message": "success", 
  "data": { ... } 
}
```

### 7.3 接口安全与权限

- **认证要求**：所有接口默认需认证（除健康检查等开放接口）
- **认证方式**：Spring Security + JWT/OAuth2 统一认证
- **令牌传递**：Token 需通过 Authorization: Bearer <token> 头部传递
- **权限控制**：支持基于角色/权限的访问控制（如 @PreAuthorize）
- **安全机制**：Token 黑名单、过期、刷新等安全机制完善
- **防护措施**：事件发布、通知发送等接口需防刷、限流

## 八、业务流程设计

### 8.1 事件发布与分发流程

```mermaid
sequenceDiagram
    participant S as 业务服务
    participant E as 事件中心
    participant K as Kafka
    participant B as 订阅服务
    S->>E: 发布事件
    E->>E: 验证事件类型
    E->>E: 持久化事件
    E->>K: 发布到消息队列
    E->>B: 分发事件到订阅方
    E->>E: 监控、告警、重试
```

### 8.2 通知发送流程

```mermaid
sequenceDiagram
    participant S as 业务服务
    participant E as 事件中心
    participant T as 模板引擎
    participant P as 外部服务
    S->>E: 发送通知请求
    E->>T: 渲染通知模板
    T->>E: 返回渲染结果
    E->>E: 持久化通知记录
    E->>P: 发送到外部服务
    E->>E: 更新发送状态
```

### 8.3 任务调度执行流程

```mermaid
sequenceDiagram
    participant U as 运维/业务服务
    participant S as 调度中心
    participant E as 事件中心
    participant T as 任务执行器
    U->>S: 注册/修改任务
    S->>S: 持久化任务、分配调度节点
    S->>S: 定时触发任务
    S->>T: 执行任务
    S->>E: 发布调度事件
    S->>S: 记录任务执行日志
    S->>S: 失败重试、告警
```

## 九、代码组织结构

### 9.1 DDD分层结构

```
aixone-event-center/
├── src/main/java/com/aixone/eventcenter/
│   ├── event/                    # 事件模块
│   │   ├── application/          # 应用层
│   │   │   ├── command/         # 命令对象
│   │   │   ├── query/           # 查询对象
│   │   │   ├── dto/             # 数据传输对象
│   │   │   └── service/         # 应用服务
│   │   ├── domain/              # 领域层
│   │   │   ├── model/           # 领域模型
│   │   │   ├── service/         # 领域服务
│   │   │   ├── repository/      # 仓储接口
│   │   │   └── event/           # 领域事件
│   │   └── infrastructure/       # 基础设施层
│   │       ├── persistence/     # 持久化
│   │       ├── messaging/       # 消息队列
│   │       └── cache/           # 缓存
│   ├── notification/            # 通知模块
│   │   ├── application/         # 应用层
│   │   ├── domain/             # 领域层
│   │   └── infrastructure/      # 基础设施层
│   │       └── external/       # 外部服务适配器
│   ├── schedule/                # 调度模块
│   │   ├── application/        # 应用层
│   │   ├── domain/            # 领域层
│   │   └── infrastructure/     # 基础设施层
│   ├── shared/                 # 共享模块
│   │   ├── common/            # 通用组件
│   │   ├── security/          # 安全组件
│   │   └── monitoring/        # 监控组件
│   └── interfaces/             # 接口层
│       ├── rest/              # REST API
│       ├── event/             # 事件监听
│       └── grpc/              # gRPC接口
├── src/test/                  # 测试代码
└── pom.xml                    # Maven配置
```

### 9.2 DDD分层说明

#### 9.2.1 应用层（Application Layer）
- **职责**：协调领域对象完成业务用例
- **组件**：应用服务、命令对象、查询对象、DTO
- **特点**：无业务逻辑，只做流程编排

#### 9.2.2 领域层（Domain Layer）
- **职责**：核心业务逻辑和规则
- **组件**：领域模型、领域服务、仓储接口、领域事件
- **特点**：业务核心，不依赖外部技术

#### 9.2.3 基础设施层（Infrastructure Layer）
- **职责**：技术实现和外部集成
- **组件**：持久化、外部服务、缓存、安全配置
- **特点**：技术实现，依赖领域层接口

#### 9.2.4 接口层（Interface Layer）
- **职责**：对外提供API和事件处理
- **组件**：REST控制器、事件监听器、gRPC服务
- **特点**：协议适配，调用应用层服务

## 十、多租户（Multi-Tenancy）支持

### 10.1 多租户定义与应用场景
多租户是指平台支持为多个独立客户（租户）提供逻辑隔离的数据、配置和权限，常见于SaaS、平台化、合规隔离等场景。每个租户的事件、通知、任务、审计日志等互相隔离，保障安全与隐私。

### 10.2 领域模型与接口扩展建议
- 所有核心实体（如 Event、Subscription、Notification、ScheduledTask、TaskExecution 等）建议增加 `tenant_id` 字段，标识所属租户。
- 所有 REST API 支持按租户维度的查询、操作，建议通过请求头、参数或Token传递租户ID。
- 事件发布、通知发送、任务调度等流程均需考虑租户隔离。

### 10.3 数据隔离与安全性
- **逻辑隔离**：通过租户ID字段实现数据隔离，所有查询、写入均需带上租户条件。
- **物理隔离（可选）**：如有更高安全需求，可采用分库分表、独立Schema等方式实现物理隔离。
- **权限隔离**：结合认证鉴权，确保租户间权限互不干扰。

### 10.4 技术实现建议
- Spring Security/Spring Cloud 支持多租户上下文传递与校验。
- 可通过拦截器、Filter、AOP等方式自动注入和校验租户ID。
- 数据库层可用MyBatis/Spring Data JPA等实现租户条件自动拼接。
- Token中携带租户信息，或通过请求头（如 X-Tenant-Id）传递。

### 10.5 推荐做法与演进
- 初期可采用逻辑隔离（租户ID字段+权限校验），满足大部分业务需求。
- 随业务发展可平滑升级为物理隔离（分库分表、独立Schema等）。
- 所有接口、日志、监控、告警等均建议支持租户维度的统计与隔离。

### 10.6 结论
本项目建议自设计之初即支持多租户能力，保障平台安全、合规与可扩展性。具体实现可根据实际业务需求逐步演进。

## 十一、研发计划与执行步骤

### 11.1 项目现状

- **项目状态**：全新项目，需要从头开发
- **架构基础**：已确定DDD四层架构和项目结构
- **技术选型**：Spring Boot 3.5+ + Spring Cloud + PostgreSQL + Redis + Kafka
- **多租户支持**：完整的多租户架构设计

### 11.2 开发阶段规划

#### 11.2.1 第一阶段：基础架构搭建（2-3周）
- [ ] 搭建项目结构
- [ ] 实现DDD四层架构基础框架
- [ ] 配置Spring Boot + Spring Cloud基础环境
- [ ] 搭建PostgreSQL + Redis + Kafka环境
- [ ] 实现基础的事件发布和订阅功能

#### 11.2.2 第二阶段：核心功能开发（4-5周）
- [ ] 实现多租户数据隔离和配置管理
- [ ] 开发事件治理功能（发布、订阅、路由、存储）
- [ ] 实现通知管理功能（多渠道通知、模板管理）
- [ ] 开发任务调度功能（定时任务、分布式调度）
- [ ] 实现基础权限校验功能

#### 11.2.3 第三阶段：高级功能开发（3-4周）
- [ ] 实现事件过滤和重试机制
- [ ] 开发通知模板引擎和变量替换
- [ ] 实现任务分片和负载均衡
- [ ] 开发多级缓存策略（多租户缓存隔离）
- [ ] 实现审计日志和监控功能

#### 11.2.4 第四阶段：测试与优化（2-3周）
- [ ] 编写单元测试和集成测试
- [ ] 性能测试和优化
- [ ] 安全测试和加固
- [ ] 文档完善和API文档生成
- [ ] 部署和运维文档

### 11.3 技术实现重点

#### 11.3.1 DDD架构实现
- **领域层**：建立事件、通知、调度等聚合根
- **应用层**：实现CQRS模式，分离命令和查询
- **基础设施层**：实现仓储模式和数据持久化
- **接口层**：实现REST API和事件监听

#### 11.3.2 多租户支持
- **数据隔离**：所有表包含tenant_id字段
- **缓存隔离**：Redis Key使用tenant_id前缀
- **配置管理**：支持租户级事件中心配置
- **安全隔离**：租户级权限校验和审计

#### 11.3.3 外部服务集成
- **认证服务**：用户认证和权限校验
- **消息队列**：Kafka事件分发和Redis通知分发
- **外部通知**：邮件、短信、推送等外部服务

### 11.4 开发规范

#### 11.4.1 代码规范
- **DDD分层**：严格遵循四层架构，保持依赖方向正确
- **命名规范**：使用清晰的领域术语和业务语言
- **注释规范**：关键业务逻辑必须有详细注释
- **测试规范**：测试覆盖率不低于80%

#### 11.4.2 质量保证
- **代码审查**：所有代码必须经过同行审查
- **持续集成**：自动化构建、测试、部署
- **性能监控**：关键指标监控和告警
- **安全扫描**：定期进行安全漏洞扫描

### 11.5 风险控制

#### 11.5.1 技术风险
- **多租户数据隔离**：确保租户间数据完全隔离
- **性能优化**：缓存策略和数据库查询优化
- **安全加固**：防止SQL注入、XSS等安全漏洞

#### 11.5.2 项目风险
- **进度控制**：按阶段里程碑进行进度跟踪
- **质量保证**：每个阶段完成后进行质量评审
- **团队协作**：建立有效的沟通和协作机制

### 11.6 成功标准

#### 11.6.1 功能标准
- [ ] 支持多租户事件治理和通知管理
- [ ] 实现完整的任务调度功能
- [ ] 支持多种事件类型和通知渠道
- [ ] 与认证服务完整集成

#### 11.6.2 性能标准
- [ ] 事件发布响应时间 < 100ms
- [ ] 通知发送响应时间 < 200ms
- [ ] 支持1000+并发用户
- [ ] 系统可用性 > 99.9%

#### 11.6.3 质量标准
- [ ] 代码测试覆盖率 > 80%
- [ ] 无严重安全漏洞
- [ ] 完整的API文档
- [ ] 完善的运维文档

> 本研发计划作为项目开发的执行指南，建议每完成一个阶段及时评审和调整，确保项目按时高质量交付。

## 十二、总结

AixOne事件中心采用DDD分层架构设计，包含事件、通知、调度三个核心模块，并提供三个SDK方便其他微服务集成。通过模块化设计和清晰的领域边界，既满足初期少微服务的需求，又为后期微服务拆分做好准备。

### 12.1 核心优势

1. **统一事件治理**：提供统一的事件发布、订阅、路由和存储能力
2. **多渠道通知**：支持邮件、短信、推送等多种通知方式
3. **分布式调度**：提供可靠的任务调度和定时任务管理
4. **高可用保障**：支持高并发、高可用的事件处理
5. **可扩展性**：支持水平扩展和功能扩展
6. **SDK集成**：提供三个SDK方便微服务集成

### 12.2 技术特色

1. **DDD分层架构**：清晰的领域边界和职责分离
2. **模块化设计**：便于独立演进和微服务拆分
3. **多租户支持**：完整的多租户架构设计
4. **可观测性**：完善的监控、日志和链路追踪
5. **安全性**：多层次的安全防护机制
6. **SDK治理**：提供统一协议、上下文管理、治理能力的SDK
7. **平台级能力**：不仅仅是消息中间件封装，而是完整的平台治理能力

### 12.3 发展展望

事件中心将继续演进，支持更多事件类型和通知渠道，提升平台的事件处理能力和用户体验，为AixOne生态系统的持续发展提供坚实的事件基础设施支撑。三个SDK将不断完善，提供更强大的治理能力、更丰富的上下文管理和更完善的可观测性能力，真正实现平台级的事件治理。

