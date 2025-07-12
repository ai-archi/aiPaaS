# AixOne 企业级微服务平台总体架构设计

## 一、平台总体架构与定位

AixOne 微服务平台由 API Gateway、Auth Service、Directory Service、Event Center、LLM Serve、Embed Serve 等核心服务组成，覆盖统一入口、认证授权、组织与用户管理、事件流转、AI 能力接入、文本嵌入等企业级场景。平台采用分层解耦、统一协议、可插拔扩展的架构理念，强调多租户、权限隔离、可观测性与高可用。

- API Gateway 作为统一入口，负责路由、聚合、限流、认证等。
- Auth Service 提供统一认证、Token、OAuth2、权限校验。
- Directory Service 作为主数据中心，管理租户、组织、用户、角色等。
- Event Center 支撑事件流转、审计、调度、监控等基础设施。
- LLM Serve/Embed Serve 提供大模型与文本嵌入等智能服务。

**小结：** 平台各服务分工明确，协同支撑企业级多租户、权限、智能、审计等核心能力。

---

## 二、各服务功能与边界

### 1. API Gateway
- 统一入口，动态路由、服务发现、限流熔断、健康检查。
- 集成统一认证、IP 黑名单、接口签名、灰度发布等。

### 2. Auth Service
- 用户登录、Token 颁发与校验、OAuth2 授权、权限控制。
- 多种认证方式，支持多租户注册开关。
- 只做认证与权限校验，主数据只读引用 Directory Service。

### 3. Directory Service
- 管理租户、组织、部门、岗位、用户、群组、角色等主数据。
- 负责主数据唯一性、关系维护、变更同步。
- 支持多租户、复杂组织结构、JPA 多对多关系。

### 4. Event Center
- 事件发布、订阅、分发、持久化、审计、调度、监控。
- 支持多种事件类型与分发方式，集成 Redis、HTTP、Prometheus。
- 子模块包括 event、subscription、audit、monitor、schedule。

### 5. LLM Serve
- 统一大模型接入与管理，OpenAI API 兼容。
- 多模型、多厂商统一接入，参数配置、密钥管理、配额控制。
- 运营分析、SLA 保障、监控告警。

### 6. Embed Serve
- 文本嵌入 API，支持多模型、多后端。
- FastAPI 实现，Nacos 注册、健康检查、LRU 缓存。

**小结：** 各服务边界清晰，主数据、认证、事件、智能等能力分层解耦，便于扩展与维护。

---

## 三、统一技术栈与基础设施

- 语言与框架：Java 21+/Spring Boot 3.x（主）、Python 3.8+/FastAPI（嵌入服务）
- 服务注册/发现：Nacos
- 配置中心：Nacos Config
- 存储：PostgreSQL/MySQL（主数据）、Redis（缓存/限流/分布式协调）
- 监控与告警：Spring Boot Actuator、Prometheus、Grafana
- 限流熔断：Sentinel
- 构建工具：Maven
- 部署：Docker/K8s

**小结：** 平台统一采用主流开源技术栈，强调可观测性、弹性伸缩与云原生部署。

---

## 四、服务间协作与最佳实践

- 认证与主数据解耦：Auth Service 只做认证与权限，主数据由 Directory Service 维护，通过 API/消息机制同步。
- 多租户与上下文：统一 session 包（aixone-session），ThreadLocal+拦截器自动注入租户、用户、权限属性。
- 事件驱动：Event Center 作为平台级事件枢纽，支撑主数据变更、审计、调度等跨服务场景。
- 监控与告警：各服务暴露标准监控接口，采集与告警由外部系统统一负责。
- API 规范与错误码：所有服务统一响应体、错误码、接口文档规范。

**小结：** 服务间通过标准协议、事件、上下文解耦协作，提升平台一致性与可维护性。

---

## 五、未来规划与扩展建议

- 支持更多 AI 能力（如多模态、RAG、Agent 等）与模型后端。
- 事件中心支持 MQ/Kafka 等更高吞吐的分发通道。
- 目录服务支持更复杂的组织关系与权限模型。
- 统一 DevOps、自动化测试与 CI/CD 流程。
- 加强平台安全、合规与多云适配能力。

**小结：** 平台架构具备良好扩展性，建议持续演进智能、事件、组织、运维等能力，支撑企业级多场景需求。 