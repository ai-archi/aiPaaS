# Observability（可观测性）

## 1. 概述
可观测性是指系统能够通过**指标（Metrics）、日志（Logs）、分布式追踪（Tracing）**来监控、分析和优化微服务运行状态。它是微服务架构中保障可靠性、弹性和运维效率的核心能力。

### 核心目标
- **监控服务状态**：了解系统健康状况、性能指标  
- **快速问题排查**：定位延迟、错误和故障根因  
- **支持弹性与容错策略**：为熔断、重试、限流提供数据支撑  
- **优化系统性能**：基于指标进行容量规划和优化  

---

## 2. 可观测性关键维度

### 2.1 指标（Metrics）
- **指标类型**：
  - 系统指标：CPU、内存、磁盘、网络  
  - 服务指标：请求数、延迟、错误率  
  - 业务指标：订单量、支付成功率  
- **采集工具**：Prometheus、Telegraf  
- **可视化工具**：Grafana、Kibana  

### 2.2 日志（Logs）
- **类型**：
  - 业务日志：业务请求、事件  
  - 系统日志：容器、服务运行状态  
  - 访问日志：HTTP/gRPC 请求信息  
- **集中管理**：
  - 日志收集与存储：ELK（Elasticsearch, Logstash, Kibana）、OpenSearch  
  - 日志分析：聚合查询、告警、异常检测  

### 2.3 分布式追踪（Tracing）
- **目的**：追踪请求在微服务间的调用链，分析延迟和依赖  
- **工具**：Jaeger、Zipkin、OpenTelemetry  
- **关键指标**：
  - 延迟（Latency）  
  - 调用链深度  
  - 异常节点位置  

### 2.4 告警与报警
- **基于指标的告警**：
  - 阈值告警：CPU > 80%，错误率 > 5%  
  - 异常模式告警：延迟异常增加  
- **告警渠道**：
  - 邮件、短信  
  - 钉钉 / Slack / Teams 通知  
- **配合自动化策略**：
  - 熔断、限流、降级，减少系统风险  

---

## 3. 工具与框架

| 工具 / 框架 | 功能 | 典型用法 | 状态与说明 |
|------------|------|-----------|------------|
| **Prometheus** | 指标采集、存储与查询 | 采集服务指标 | 推荐，开源、成熟 |
| **Grafana** | 指标可视化、Dashboard | 与 Prometheus 搭配 | 推荐，支持多数据源 |
| **ELK / OpenSearch** | 日志收集与分析 | 日志聚合、检索、分析 | 标准日志解决方案 |
| **Jaeger / Zipkin** | 分布式追踪 | 调用链分析 | 推荐，支持 OpenTracing/OpenTelemetry |
| **OpenTelemetry** | 统一采集指标、日志和追踪 | 统一标准 | 新一代标准，跨语言支持 |

---

## 4. 架构示意图（Mermaid）

```mermaid
graph TD
    ServiceA[服务A] -->|HTTP/gRPC| ServiceB[服务B]
    ServiceA -->|指标/日志| Prometheus
    ServiceB -->|指标/日志| Prometheus
    ServiceA -->|Trace| Jaeger
    ServiceB -->|Trace| Jaeger
    Prometheus --> Grafana[可视化Dashboard]
    Jaeger --> Grafana
````

---

## 5. 行业最佳实践

1. **统一指标与日志采集**：Prometheus + Grafana + ELK/OpenSearch，覆盖所有服务
2. **关键业务路径追踪**：在核心业务链路打标签，监控延迟和异常
3. **合理告警策略**：避免告警风暴，设置阈值和聚合策略
4. **自动化与弹性结合**：指标驱动自动触发熔断、重试、降级策略
5. **数据保留策略**：日志、指标和追踪数据按重要性分级存储
6. **跨集群和跨数据中心可观测性**：统一监控视图，支持多集群部署

---

## 6. 参考资料

* [Prometheus 文档](https://prometheus.io/docs/introduction/overview/)
* [Grafana 文档](https://grafana.com/docs/)
* [Jaeger 分布式追踪](https://www.jaegertracing.io/docs/)
* [OpenTelemetry](https://opentelemetry.io/)
* [Observability in Microservices](https://www.cncf.io/blog/2020/04/07/observability-for-microservices/)
