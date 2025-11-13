# AixOne事件中心开发计划

## 一、项目现状分析

### 1.1 已完成功能

#### 事件模块（完整实现）
- ✅ Event聚合根（包含status字段，字段命名统一，JSONB类型）
- ✅ Subscription聚合根（完整实现）
- ✅ Topic聚合根（完整实现）
- ✅ EventApplicationService（完整功能，包含事件路由）
- ✅ SubscriptionApplicationService（完整实现）
- ✅ TopicApplicationService（完整实现）
- ✅ EventRoutingService（事件路由和分发服务）
- ✅ EventRouter（事件路由领域服务）
- ✅ EventDistributor（事件分发器）
- ✅ EventController（API路径：`/api/v1/events`）
- ✅ SubscriptionController（API路径：`/api/v1/events/subscriptions`）
- ✅ TopicController（API路径：`/api/v1/topics`）
- ✅ EventAdminController（管理接口：`/api/v1/admin/events`）
- ✅ SubscriptionAdminController（管理接口：`/api/v1/admin/events/subscriptions`）
- ✅ TopicAdminController（管理接口：`/api/v1/admin/topics`）
- ✅ KafkaEventPublisher（基础设施）
- ✅ JpaEventRepository、JpaSubscriptionRepository、JpaTopicRepository（持久化）

#### 通知模块（完整实现）
- ✅ Notification聚合根（完整实现，包含所有字段和枚举）
- ✅ NotificationTemplate聚合根（完整实现）
- ✅ NotificationRepository（完整实现）
- ✅ NotificationTemplateRepository（完整实现）
- ✅ NotificationApplicationService（完整实现，支持模板发送）
- ✅ TemplateApplicationService（完整实现，支持模板管理）
- ✅ CompositeNotificationSender（组合发送器）
- ✅ EmailNotificationSender（邮件发送器）
- ✅ SmsNotificationSender（短信发送器）
- ✅ PushNotificationSender（推送发送器）
- ✅ TemplateEngine（模板引擎，支持{{variable}}格式）
- ✅ NotificationController（业务接口：`/api/v1/notifications`）
- ✅ TemplateController（业务接口：`/api/v1/notifications/templates`）
- ✅ NotificationAdminController（管理接口：`/api/v1/admin/notifications`）
- ✅ TemplateAdminController（管理接口：`/api/v1/admin/notifications/templates`）
- ✅ JpaNotificationRepository、JpaNotificationTemplateRepository（持久化）

#### 调度模块（基本实现）
- ✅ Task聚合根（完整实现）
- ✅ TaskLog实体（完整实现）
- ✅ TaskApplicationService（完整实现）
- ✅ TaskSchedulerService（完整实现）
- ✅ TaskController（API路径：`/api/v1/schedule/tasks`）
- ✅ TaskAdminController（管理接口：`/api/v1/admin/schedule/tasks`）
- ✅ ScheduleMonitorController（API路径：`/api/v1/schedule/monitor`）
- ✅ QuartzTaskScheduler（基础设施）
- ✅ JpaTaskRepository、JpaTaskLogRepository（持久化）

#### 单元测试
- ✅ Event实体测试（包含状态管理测试）
- ✅ Subscription实体测试
- ✅ Notification实体测试
- ✅ NotificationTemplate实体测试
- ✅ EventApplicationService测试
- ✅ SubscriptionApplicationService测试
- ✅ NotificationApplicationService测试
- ✅ TemplateApplicationService测试
- ✅ EventRouterImpl测试
- ✅ EventRoutingService测试
- ✅ TemplateEngine测试

### 1.2 待完善功能

#### 事件模块
- ⚠️ 事件过滤机制（基础实现完成，可进一步优化）
- ⚠️ 事件重试策略（需要实现）

#### 通知模块
- ⚠️ 通知发送重试机制（需要实现）
- ⚠️ 通知发送限流（需要实现）
- ⚠️ 短信和推送的实际服务集成（目前是模拟实现）

#### 调度模块
- ⚠️ 任务分片功能（需要实现）
- ⚠️ 任务负载均衡（需要实现）
- ⚠️ 任务故障转移（需要实现）

#### 通用问题
- ⚠️ 集成测试（需要实现）
- ⚠️ 性能测试（需要实现）
- ⚠️ 数据库迁移脚本（需要创建）

## 二、开发计划

### 阶段一：代码重构与完善（1-2周）

#### 1.1 修复目录结构（1天）

**任务清单**：
- [ ] 删除`event/EventService.java`（功能已由EventApplicationService提供）
- [ ] 删除`event/EventRepository.java`（已由domain/EventRepository.java提供）
- [ ] 确认所有代码文件都在正确的DDD分层目录中

**验收标准**：
- 所有代码文件符合DDD四层架构
- 无重复的Service和Repository

#### 1.2 完善Event实体模型（2天）

**任务清单**：
- [ ] 添加status字段（EventStatus枚举：PENDING、PUBLISHED、FAILED、CANCELLED）
- [ ] 统一字段命名：
  - `source` → `eventSource`（数据库字段：`event_source`）
  - `data` → `eventData`（数据库字段：`event_data`，类型改为JSONB）
  - `timestamp` → `createdAt`（数据库字段：`created_at`）
- [ ] 更新JPA注解，明确指定数据库字段名
- [ ] 更新EventApplicationService以支持status管理
- [ ] 创建数据库迁移脚本

**验收标准**：
- Event实体字段与设计文档一致
- 数据库字段类型正确（JSONB）
- 所有相关代码已更新

#### 1.3 实现Subscription功能（3-4天）

**任务清单**：
- [ ] 创建Subscription聚合根（domain层）
  - 字段：subscriptionId、tenantId、eventType、subscriberService、subscriberEndpoint、status、filterConfig、retryConfig、createdAt
- [ ] 创建SubscriptionRepository接口（domain层）
- [ ] 创建SubscriptionApplicationService（application层）
  - 功能：创建订阅、更新订阅、取消订阅、查询订阅列表、查询订阅详情
- [ ] 创建SubscriptionController（interfaces层）
  - 业务接口：`/api/v1/events/subscriptions`
  - 管理接口：`/api/v1/admin/events/subscriptions`
- [ ] 实现JpaSubscriptionRepository（infrastructure层）
- [ ] 实现事件路由和分发机制
- [ ] 创建数据库表：`subscriptions`

**验收标准**：
- Subscription功能完整实现
- 支持动态订阅、取消订阅、订阅配置
- 支持事件路由和分发

#### 1.4 实现管理接口（2-3天）

**任务清单**：
- [ ] EventController：添加管理接口（`/api/v1/admin/events`）
- [ ] TopicController：添加管理接口（`/api/v1/admin/topics`）
- [ ] TaskController：添加管理接口（`/api/v1/admin/schedule/tasks`）
- [ ] 所有管理接口支持tenantId查询参数
- [ ] 所有管理接口添加权限验证（`admin:{resource}:{action}`）

**验收标准**：
- 所有模块都有完整的管理接口
- 管理接口支持跨租户查询
- 权限验证正确实现

---

### 阶段二：通知模块开发（2-3周）

#### 2.1 通知模块领域层（3-4天）

**任务清单**：
- [ ] 创建Notification聚合根
  - 字段：notificationId、tenantId、notificationType、recipientInfo（JSONB）、notificationContent（JSONB）、status、priority、channel、createdAt、sentAt
- [ ] 创建NotificationTemplate聚合根
  - 字段：templateId、tenantId、templateName、notificationType、subjectTemplate、bodyTemplate、channels、variables、version、createdAt
- [ ] 创建NotificationChannel枚举（EMAIL、SMS、PUSH、IM等）
- [ ] 创建NotificationStatus枚举（PENDING、SENT、FAILED、CANCELLED）
- [ ] 创建NotificationRepository接口
- [ ] 创建NotificationTemplateRepository接口
- [ ] 创建数据库表：`notifications`、`notification_templates`

**验收标准**：
- 领域模型完整，符合设计文档
- 数据库表结构正确

#### 2.2 通知模块应用层（3-4天）

**任务清单**：
- [ ] 创建NotificationApplicationService
  - 功能：发送通知、查询通知列表、查询通知详情、更新通知状态
- [ ] 创建TemplateApplicationService
  - 功能：创建模板、更新模板、删除模板、查询模板列表、查询模板详情、渲染模板
- [ ] 创建Command对象：
  - SendNotificationCommand
  - CreateTemplateCommand
  - UpdateTemplateCommand
- [ ] 创建DTO对象：
  - NotificationDto
  - NotificationTemplateDto

**验收标准**：
- 应用服务功能完整
- Command和DTO设计合理

#### 2.3 通知模块基础设施层（4-5天）

**任务清单**：
- [ ] 实现JpaNotificationRepository
- [ ] 实现JpaTemplateRepository
- [ ] 创建NotificationSender接口
- [ ] 实现EmailNotificationSender（SMTP邮件发送）
- [ ] 实现SmsNotificationSender（集成阿里云SMS/腾讯云SMS）
- [ ] 实现PushNotificationSender（集成极光推送/个推）
- [ ] 实现ImNotificationSender（集成微信/钉钉）
- [ ] 创建TemplateEngine（模板引擎，支持变量替换、条件判断、循环）
- [ ] 实现通知发送策略（优先级、限流、重试）

**验收标准**：
- 多渠道通知发送器实现完整
- 模板引擎功能完整
- 发送策略正确实现

#### 2.4 通知模块接口层（2-3天）

**任务清单**：
- [ ] 创建NotificationController
  - 业务接口：`/api/v1/notifications`
  - 管理接口：`/api/v1/admin/notifications`
- [ ] 创建TemplateController
  - 业务接口：`/api/v1/notifications/templates`
  - 管理接口：`/api/v1/admin/notifications/templates`
- [ ] 实现所有REST接口（POST、GET、PUT、DELETE）
- [ ] 添加权限验证

**验收标准**：
- 所有接口符合设计文档
- 接口功能完整
- 权限验证正确

---

### 阶段三：功能完善与优化（1-2周）

#### 3.1 事件模块功能完善（3-4天）

**任务清单**：
- [ ] 实现事件过滤机制
- [ ] 实现事件重试策略
- [ ] 实现事件路由规则
- [ ] 优化事件查询性能（索引优化）
- [ ] 实现事件数据归档功能

**验收标准**：
- 事件过滤和重试功能正常
- 查询性能满足要求

#### 3.2 通知模块功能完善（2-3天）

**任务清单**：
- [ ] 实现通知模板版本管理
- [ ] 实现通知A/B测试
- [ ] 实现通知发送状态跟踪
- [ ] 实现通知批量发送
- [ ] 实现通知发送限流

**验收标准**：
- 模板版本管理功能正常
- 批量发送功能正常

#### 3.3 调度模块功能完善（2-3天）

**任务清单**：
- [ ] 实现任务分片功能
- [ ] 实现任务负载均衡
- [ ] 实现任务故障转移
- [ ] 优化任务执行性能
- [ ] 完善任务监控功能

**验收标准**：
- 任务分片和负载均衡功能正常
- 故障转移机制可靠

#### 3.4 多租户支持完善（2天）

**任务清单**：
- [ ] 确保所有查询接口自动过滤tenantId
- [ ] 确保所有创建/更新接口自动设置tenantId
- [ ] 实现Redis缓存隔离（Key使用tenant_id前缀）
- [ ] 实现多租户配置管理（如果需要）

**验收标准**：
- 多租户数据隔离正确
- 缓存隔离正确

---

### 阶段四：测试与文档（1-2周）

#### 4.1 单元测试（3-4天）

**任务清单**：
- [ ] 事件模块单元测试（覆盖率>80%）
- [ ] 通知模块单元测试（覆盖率>80%）
- [ ] 调度模块单元测试（覆盖率>80%）
- [ ] 所有应用服务测试
- [ ] 所有领域服务测试

**验收标准**：
- 测试覆盖率>80%
- 所有关键业务逻辑有测试覆盖

#### 4.2 集成测试（2-3天）

**任务清单**：
- [ ] API接口集成测试
- [ ] 多租户隔离测试
- [ ] 事件发布订阅集成测试
- [ ] 通知发送集成测试
- [ ] 任务调度集成测试

**验收标准**：
- 所有集成测试通过
- 多租户隔离验证正确

#### 4.3 性能测试（2天）

**任务清单**：
- [ ] 事件发布性能测试（目标：<100ms）
- [ ] 通知发送性能测试（目标：<200ms）
- [ ] 并发测试（目标：支持1000+并发用户）
- [ ] 数据库查询性能优化

**验收标准**：
- 性能指标达到设计要求
- 数据库查询优化完成

#### 4.4 文档完善（2-3天）

**任务清单**：
- [ ] API文档生成（Swagger/OpenAPI）
- [ ] 部署文档
- [ ] 运维文档
- [ ] 开发指南
- [ ] 故障排查指南

**验收标准**：
- 文档完整、准确
- API文档可在线查看

---

## 三、详细任务清单

### 3.1 事件模块任务

| 任务ID | 任务名称 | 优先级 | 预计工时 | 依赖关系 |
|--------|---------|--------|----------|----------|
| E-001 | 修复目录结构 | 高 | 0.5天 | - |
| E-002 | 完善Event实体模型 | 高 | 2天 | E-001 |
| E-003 | 实现Subscription聚合根 | 高 | 1天 | E-002 |
| E-004 | 实现SubscriptionRepository | 高 | 0.5天 | E-003 |
| E-005 | 实现SubscriptionApplicationService | 高 | 1.5天 | E-004 |
| E-006 | 实现SubscriptionController | 高 | 1天 | E-005 |
| E-007 | 实现事件路由和分发 | 高 | 1.5天 | E-006 |
| E-008 | 实现管理接口 | 中 | 1天 | E-006 |
| E-009 | 实现事件过滤机制 | 中 | 1.5天 | E-007 |
| E-010 | 实现事件重试策略 | 中 | 1天 | E-007 |

### 3.2 通知模块任务

| 任务ID | 任务名称 | 优先级 | 预计工时 | 依赖关系 |
|--------|---------|--------|----------|----------|
| N-001 | 创建Notification聚合根 | 高 | 1天 | - |
| N-002 | 创建NotificationTemplate聚合根 | 高 | 1天 | - |
| N-003 | 创建NotificationRepository | 高 | 0.5天 | N-001 |
| N-004 | 创建TemplateRepository | 高 | 0.5天 | N-002 |
| N-005 | 实现NotificationApplicationService | 高 | 2天 | N-003 |
| N-006 | 实现TemplateApplicationService | 高 | 2天 | N-004 |
| N-007 | 实现EmailNotificationSender | 高 | 1.5天 | N-005 |
| N-008 | 实现SmsNotificationSender | 高 | 1.5天 | N-005 |
| N-009 | 实现PushNotificationSender | 高 | 1.5天 | N-005 |
| N-010 | 实现TemplateEngine | 高 | 2天 | N-006 |
| N-011 | 实现NotificationController | 高 | 1.5天 | N-005 |
| N-012 | 实现TemplateController | 高 | 1天 | N-006 |
| N-013 | 实现管理接口 | 中 | 1天 | N-011, N-012 |
| N-014 | 实现通知发送策略 | 中 | 1.5天 | N-007, N-008, N-009 |

### 3.3 调度模块任务

| 任务ID | 任务名称 | 优先级 | 预计工时 | 依赖关系 |
|--------|---------|--------|----------|----------|
| S-001 | 实现管理接口 | 中 | 1天 | - |
| S-002 | 实现任务分片 | 中 | 2天 | - |
| S-003 | 实现任务负载均衡 | 中 | 1.5天 | S-002 |
| S-004 | 实现任务故障转移 | 中 | 2天 | S-003 |

### 3.4 通用任务

| 任务ID | 任务名称 | 优先级 | 预计工时 | 依赖关系 |
|--------|---------|--------|----------|----------|
| G-001 | 多租户支持完善 | 高 | 2天 | E-002, N-001 |
| G-002 | 单元测试 | 高 | 4天 | 所有模块完成 |
| G-003 | 集成测试 | 高 | 3天 | G-002 |
| G-004 | 性能测试 | 中 | 2天 | G-003 |
| G-005 | 文档完善 | 中 | 3天 | 所有功能完成 |

---

## 四、开发优先级

### 4.1 P0（必须完成，阻塞发布）

1. **修复目录结构**（E-001）
2. **完善Event实体模型**（E-002）
3. **实现Subscription功能**（E-003 ~ E-007）
4. **实现通知模块核心功能**（N-001 ~ N-012）
5. **实现管理接口**（E-008, N-013, S-001）
6. **多租户支持完善**（G-001）
7. **单元测试**（G-002）

### 4.2 P1（重要功能，建议完成）

1. **事件过滤和重试**（E-009, E-010）
2. **通知发送策略**（N-014）
3. **任务分片和负载均衡**（S-002, S-003, S-004）
4. **集成测试**（G-003）

### 4.3 P2（优化功能，可选）

1. **性能测试**（G-004）
2. **文档完善**（G-005）

---

## 五、里程碑计划

### 里程碑1：基础功能完成（3-4周）

**目标**：
- 事件模块核心功能完成（包括Subscription）
- 通知模块核心功能完成
- 调度模块管理接口完成
- 多租户支持完善

**验收标准**：
- 所有P0任务完成
- 单元测试覆盖率>80%
- 基础功能可正常使用

### 里程碑2：功能完善（5-6周）

**目标**：
- 所有P1任务完成
- 集成测试通过
- 性能测试通过

**验收标准**：
- 所有P1任务完成
- 集成测试全部通过
- 性能指标达到要求

### 里程碑3：发布准备（6-7周）

**目标**：
- 所有P2任务完成
- 文档完善
- 准备发布

**验收标准**：
- 所有任务完成
- 文档完整
- 可以发布

---

## 六、风险控制

### 6.1 技术风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 通知模块外部服务集成复杂 | 高 | 先实现接口，外部服务集成分阶段进行 |
| 事件路由和分发性能问题 | 中 | 提前进行性能测试，优化路由算法 |
| 多租户数据隔离问题 | 高 | 严格测试，确保所有查询都带tenantId |

### 6.2 进度风险

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 通知模块开发时间超期 | 中 | 优先实现核心功能，高级功能后续迭代 |
| 测试时间不足 | 中 | 开发过程中同步编写测试，避免后期集中测试 |

---

## 七、资源需求

### 7.1 开发人员

- **后端开发**：2-3人
- **测试人员**：1人
- **DevOps**：0.5人（支持）

### 7.2 基础设施

- PostgreSQL数据库
- Redis缓存
- Kafka消息队列
- 外部服务（邮件、短信、推送等）

---

## 八、成功标准

### 8.1 功能标准

- [ ] 事件发布、订阅、查询功能完整
- [ ] 通知发送、模板管理功能完整
- [ ] 任务调度功能完整
- [ ] 多租户数据隔离正确
- [ ] 管理接口功能完整

### 8.2 性能标准

- [ ] 事件发布响应时间 < 100ms
- [ ] 通知发送响应时间 < 200ms
- [ ] 支持1000+并发用户
- [ ] 系统可用性 > 99.9%

### 8.3 质量标准

- [ ] 代码测试覆盖率 > 80%
- [ ] 无严重安全漏洞
- [ ] 完整的API文档
- [ ] 完善的运维文档

---

## 九、执行建议

1. **按阶段执行**：严格按照阶段顺序执行，确保基础功能先完成
2. **并行开发**：通知模块和事件模块完善可以并行进行
3. **持续测试**：开发过程中持续编写测试，避免后期集中测试
4. **代码审查**：每个任务完成后进行代码审查
5. **及时调整**：根据实际情况及时调整计划

---

> 本开发计划基于当前代码状态和设计文档要求制定，建议每周评审一次，根据实际情况调整计划。

