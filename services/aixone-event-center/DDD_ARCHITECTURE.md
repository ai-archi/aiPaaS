# aixone-event-center DDD 架构设计

## 概述

本项目采用领域驱动设计（DDD）的模块化分层架构，将系统按照业务领域划分为独立模块，每个模块内部采用 DDD 的四层架构模式。

## 模块划分

### 1. 事件模块 (event)
**职责**: 事件发布、订阅、Topic管理、Kafka集成
**核心聚合**: Event、Topic、EventType

### 2. 通知模块 (notification)  
**职责**: 通知消息的创建、发送、管理
**核心聚合**: Notification

### 3. 调度模块 (schedule)
**职责**: 定时任务调度、执行、监控
**核心聚合**: ScheduleTask、JobLog、Scheduler

### 4. 共享内核 (shared)
**职责**: 提供跨模块的通用领域概念和基础设施
**核心组件**: Entity、ValueObject、DomainEvent、Repository

## DDD 分层架构

每个模块内部采用标准的 DDD 四层架构：

```
模块名/
├── domain/          # 领域层
│   ├── 聚合根
│   ├── 实体
│   ├── 值对象
│   ├── 领域服务
│   └── 仓储接口
├── application/     # 应用层
│   ├── 应用服务
│   ├── 命令处理器
│   └── 查询处理器
├── infrastructure/  # 基础设施层
│   ├── 仓储实现
│   ├── 外部服务适配器
│   └── 技术组件
└── interfaces/      # 接口层
    ├── REST控制器
    ├── 事件处理器
    └── DTO对象
```

## 详细模块设计

### 事件模块 (event)

#### 领域层
- **Event**: 事件聚合根，包含事件ID、类型、数据、时间戳等
- **Topic**: Topic聚合根，管理Kafka Topic的元数据
- **EventType**: 事件类型值对象，定义事件的分类和版本
- **EventRepository**: 事件仓储接口
- **TopicRepository**: Topic仓储接口

#### 应用层
- **EventApplicationService**: 事件应用服务，协调事件发布和查询
- **TopicApplicationService**: Topic应用服务，管理Topic生命周期

#### 基础设施层
- **JpaEventRepository**: JPA事件仓储实现
- **JpaTopicRepository**: JPA Topic仓储实现
- **KafkaEventPublisher**: Kafka事件发布器
- **KafkaTopicManager**: Kafka Topic管理器

#### 接口层
- **EventController**: 事件REST接口
- **TopicController**: Topic管理REST接口

### 通知模块 (notification)

#### 领域层
- **Notification**: 通知聚合根，包含标题、内容、类型、优先级等
- **NotificationRepository**: 通知仓储接口

#### 应用层
- **NotificationApplicationService**: 通知应用服务，协调通知创建和发送

#### 基础设施层
- **JpaNotificationRepository**: JPA通知仓储实现
- **NotificationSender**: 通知发送器
- **EmailNotificationService**: 邮件通知服务
- **SmsNotificationService**: 短信通知服务
- **PushNotificationService**: 推送通知服务
- **WebhookNotificationService**: Webhook通知服务

#### 接口层
- **NotificationController**: 通知REST接口

### 调度模块 (schedule)

#### 领域层
- **ScheduleTask**: 调度任务聚合根，包含任务配置和执行状态
- **JobLog**: 任务执行日志实体，记录执行历史
- **Scheduler**: 调度器节点实体，管理调度器实例
- **ScheduleTaskRepository**: 任务仓储接口
- **JobLogRepository**: 日志仓储接口
- **SchedulerRepository**: 调度器仓储接口

#### 应用层
- **ScheduleApplicationService**: 调度应用服务，协调任务调度和执行

#### 基础设施层
- **TaskExecutor**: 任务执行器
- **HttpTaskExecutor**: HTTP任务执行器
- **MessageTaskExecutor**: 消息任务执行器
- **DataSyncTaskExecutor**: 数据同步任务执行器
- **FileProcessTaskExecutor**: 文件处理任务执行器
- **CustomTaskExecutor**: 自定义任务执行器

#### 接口层
- **ScheduleController**: 调度管理REST接口

### 共享内核 (shared)

#### 领域层
- **Entity**: 实体基类，提供ID和相等性比较
- **ValueObject**: 值对象基类，定义不可变对象
- **DomainEvent**: 领域事件基类，表示领域中的重要事件
- **Repository**: 仓储接口基类，定义通用持久化操作

## 核心设计原则

### 1. 聚合设计
- 每个聚合根负责维护其内部的一致性边界
- 聚合根通过ID引用其他聚合，不直接持有其他聚合的引用
- 聚合内部实体和值对象通过聚合根访问

### 2. 领域事件
- 使用领域事件表示聚合状态变化
- 事件发布在应用服务层，由基础设施层处理
- 支持跨模块的事件通信

### 3. 仓储模式
- 领域层定义仓储接口，基础设施层提供实现
- 仓储只返回聚合根，不返回内部实体
- 支持复杂的查询需求

### 4. 应用服务
- 协调多个聚合完成业务用例
- 不包含业务逻辑，只负责流程编排
- 处理事务边界和领域事件发布

### 5. 依赖倒置
- 领域层不依赖任何外部技术
- 基础设施层实现领域层定义的接口
- 接口层依赖应用层，不直接访问领域层

## 技术实现

### 持久化
- 使用JPA/Hibernate进行数据持久化
- 每个聚合对应独立的数据库表
- 支持多租户数据隔离

### 消息队列
- 集成Kafka进行事件发布和订阅
- 支持Topic的自动创建和管理
- 提供可靠的消息传递保证

### 任务调度
- 基于Spring的@Scheduled注解实现定时调度
- 支持分布式任务执行
- 提供任务执行监控和重试机制

### 通知发送
- 支持多种通知渠道（邮件、短信、推送、Webhook）
- 可扩展的通知发送器架构
- 支持通知状态跟踪和重试

## 模块间通信

### 1. 同步通信
- 通过应用服务接口进行模块间调用
- 使用REST API进行跨模块通信
- 保持模块间的松耦合

### 2. 异步通信
- 通过领域事件进行模块间通信
- 使用Kafka进行事件发布和订阅
- 支持最终一致性

### 3. 数据共享
- 通过共享内核提供通用领域概念
- 避免模块间的直接数据依赖
- 保持模块的独立性

## 扩展性设计

### 1. 水平扩展
- 每个模块可以独立部署和扩展
- 支持微服务架构演进
- 提供模块级别的负载均衡

### 2. 功能扩展
- 通过实现接口添加新的通知渠道
- 通过继承基类添加新的任务类型
- 通过配置支持新的消息中间件

### 3. 数据扩展
- 支持多租户数据隔离
- 提供数据迁移和版本管理
- 支持分库分表扩展

## 监控和运维

### 1. 健康检查
- 每个模块提供独立的健康检查接口
- 支持依赖服务的健康状态监控
- 提供详细的错误信息

### 2. 指标监控
- 使用Spring Boot Actuator提供运行时指标
- 支持Prometheus指标收集
- 提供自定义业务指标

### 3. 日志管理
- 结构化日志输出
- 支持分布式链路追踪
- 提供日志聚合和查询

## 总结

本架构设计遵循DDD的核心原则，通过模块化和分层实现了高内聚、低耦合的系统架构。每个模块都有明确的职责边界，支持独立开发、测试和部署。通过共享内核和标准化的接口设计，确保了模块间的良好协作和系统的整体一致性。
